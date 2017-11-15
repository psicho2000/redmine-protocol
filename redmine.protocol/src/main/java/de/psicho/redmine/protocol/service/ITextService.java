package de.psicho.redmine.protocol.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Rectangle;
import com.taskadapter.redmineapi.bean.Issue;

import de.psicho.redmine.iTextile.DocumentCreationException;
import de.psicho.redmine.iTextile.iTextile;
import de.psicho.redmine.iTextile.command.TextProperty;
import de.psicho.redmine.protocol.api.IssueHandler;
import de.psicho.redmine.protocol.config.AppConfig;
import de.psicho.redmine.protocol.config.Protocol;
import de.psicho.redmine.protocol.model.AttachedFile;
import de.psicho.redmine.protocol.model.IssueJournalWrapper;
import de.psicho.redmine.protocol.utils.DateUtils;
import net.java.textilej.parser.markup.textile.TextileDialect;

@Component
public class ITextService {

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private IssueHandler issueHandler;

    private String issueLinkPrefix;

    @Autowired
    private ProtocolService protocolService;

    private Protocol redmineProtocol;

    // TODO autowire as a (stateful --> prototype?) bean; how to inject filename (known at runtime, not compile time)
    // REMARK: if this is prototype, the whole chain must be!
    private iTextile iTextile;

    public void endTable() {
        iTextile.endTable();
    }

    public void finalizeITextile(String footer) throws DocumentCreationException, FileNotFoundException, DocumentException {
        iTextile.setFooter(footer);
        iTextile.createFile();
    }

    public void processStatus(Issue protocol, List<IssueJournalWrapper> statusJournals) {
        String statusContent = "*Status vom letzten Protokoll*\r\n"
            + statusJournals.stream().map(this::appendJournal).collect(Collectors.joining("\r\n"));
        statusContent = postProcessContent(statusContent);
        iTextile.addTableRow("---", statusContent,
            protocolService.getProtocolUser(protocol, redmineProtocol.getFields().getModeration()));
    }

    public Set<AttachedFile> processTop(List<IssueJournalWrapper> topJournals) {
        Set<AttachedFile> result = new HashSet<>();
        for (IssueJournalWrapper top : topJournals) {
            String number = setIssueLinks("#" + top.getIssueId().toString());
            String title = "*" + top.getIssueSubject().trim() + "*\r\n";
            String content;
            if (top.getJournal() != null) {
                content = top.getJournal().getNotes();
            } else {
                Issue issue = issueHandler.getIssue(top.getIssueId());
                content = issue.getDescription();
            }
            result.addAll(extractAttachments(content, top.getIssueId()));
            content = postProcessContent(content);
            Integer assignee = top.getAssignee();
            if (assignee == null || assignee == 0) {
                throw new RuntimeException(String.format("Das Ticket #%d wurde niemandem zugewiesen!", top.getIssueId()));
            }
            String responsible = protocolService.getProtocolUser(assignee);
            iTextile.addTableRow(number, title + content, responsible);
        }
        return result;
    }

    public void startITextile(String filename) {
        File file = new File(filename);
        file.getParentFile().mkdirs();
        iTextile = new iTextile(filename);
    }

    public void startTable(Issue protocol) {
        iTextile.startTable(3);
        iTextile.setTableColumnWidth(0, 35f);
        iTextile.setTableColumnWidth(2, 70f);
        iTextile.setTableColumnParser(0, new TextileDialect());
        iTextile.setTableColumnParser(1, new TextileDialect());

        TextProperty format = TextProperty.builder().style(Font.BOLD).build();
        iTextile.setTableHeader(format, BaseColor.GRAY, "Nr.", "TOP / Beschluss", "Verantw.");

        String moderation = protocolService.getProtocolUser(protocol, redmineProtocol.getFields().getModeration());
        String devotion = protocolService.getProtocolUser(protocol, redmineProtocol.getFields().getDevotion());
        iTextile.addTableRow("---", "Moderation / Andacht", moderation + "/ " + devotion);
    }

    public void writeDocumentHeader(Issue protocol) {
        heading();
        Date protocolStartDate = protocol.getStartDate();

        String title =
            new StringBuilder().append("Gemeinderat am ").append(DateUtils.dateToGer(protocolStartDate)).append(" bei ")
                .append(protocolService.getProtocolValue(protocol, redmineProtocol.getFields().getLocation())).toString();
        iTextile.startTable(2, Rectangle.NO_BORDER);
        iTextile.setTableColumnFormat(0, TextProperty.builder().alignment(Element.ALIGN_LEFT).build());
        iTextile.setTableColumnFormat(1, TextProperty.builder().alignment(Element.ALIGN_RIGHT).build());
        iTextile.setTableColumnWidth(0, 460);
        iTextile.addTableRow(title, protocolService.getProtocolValue(protocol, redmineProtocol.getFields().getNumber()));
        iTextile.endTable();

        StringBuilder meal = new StringBuilder();
        meal.append("Essen: ");
        meal.append(protocolService.getProtocolValue(protocol, redmineProtocol.getFields().getMeal()));
        paragraph(meal.toString());

        StringBuilder members = new StringBuilder();
        members.append("Anwesend: ");
        members.append(protocolService.getProtocolValue(protocol, redmineProtocol.getFields().getParticipants()));
        paragraph(members.toString());
    }

    private String appendJournal(IssueJournalWrapper status) {
        StringBuilder statusContent = new StringBuilder();
        statusContent.append("#");
        statusContent.append(status.getIssueId());
        statusContent.append(" (");
        statusContent.append(status.getIssueSubject());
        statusContent.append(")");
        statusContent.append("\r\n");
        statusContent.append(status.getJournal().getNotes());
        return statusContent.toString();
    }

    private void heading() {
        BaseColor opalBlue = new BaseColor(25, 73, 150);
        iTextile.addParagraph(insertSpaces("PROTOKOLL", 8),
            TextProperty.builder().size(18.0f).style(Font.BOLD).color(opalBlue).alignment(Element.ALIGN_CENTER).build());
    }

    @PostConstruct
    private void init() {
        redmineProtocol = appConfig.getRedmine().getProtocol();
        issueLinkPrefix = appConfig.getRedmine().getIssues().getLink();
    }

    private String insertSpaces(String content, int numberOfSpaces) {
        String spacing = StringUtils.repeat(" ", numberOfSpaces);
        String[] stringArray = content.split("");
        return StringUtils.join(stringArray, spacing);
    }

    private String markPersons(String content) {
        String replaced = content;
        for (String member : redmineProtocol.getMembers()) {
            Matcher matcher = Pattern.compile("(" + member + ")([^a-zA-ZäöüÄÖÜß]|$)").matcher(replaced);
            // dirty hack: don't use html tags - redmine.protocol may not know redmine.iTextile is using html internally
            // sadly, it does not work otherwise
            replaced = matcher.replaceAll("<b>%{background:yellow}$1%</b>$2");
        }
        return replaced;
    }

    private void paragraph(String title) {
        iTextile.addParagraph(title, TextProperty.builder().size(12.0f).style(Font.NORMAL).color(BaseColor.BLACK).build());
    }

    private String postProcessContent(String content) {
        String linked = setIssueLinks(content);
        String unattached = linked.replace("attachment:", "");
        return markPersons(unattached);
    }

    private Set<AttachedFile> extractAttachments(String content, Integer issueId) {
        Matcher matcher = Pattern.compile("attachment:\"(.*?)\"").matcher(content);
        Set<AttachedFile> result = new HashSet<>();
        while (matcher.find()) {
            String attachedFileName = matcher.group().replace("\"", "").replace("attachment:", "");
            AttachedFile attachedFile = AttachedFile.builder().issueId(issueId).fileName(attachedFileName).build();
            result.add(attachedFile);
        }
        return result;
    }

    private String setIssueLinks(String content) {
        Matcher matcher = Pattern.compile("#(\\d*)").matcher(content);
        return matcher.replaceAll("\"#$1\":" + issueLinkPrefix + "$1");
    }
}
