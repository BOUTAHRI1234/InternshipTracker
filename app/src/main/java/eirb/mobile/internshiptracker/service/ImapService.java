package eirb.mobile.internshiptracker.service;

import java.util.Properties;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.OrTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SubjectTerm;

public class ImapService {

    public interface EmailCallback {
        void onEmailFound(Message message, String folderName);
    }

    public void fetchEmails(String email, String password, EmailCallback callback) {
        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");

        try {
            Session session = Session.getDefaultInstance(props, null);
            Store store = session.getStore("imaps");
            store.connect("imap.gmail.com", email, password);

            String[] folders = {"Inbox", "[Gmail]/Sent Mail"};
            String[] keywords = {"internship", "stage", "PFE", "application"};

            for (String folderName : folders) {
                try {
                    Folder folder = store.getFolder(folderName);
                    if (!folder.exists()) continue;

                    folder.open(Folder.READ_ONLY);

                    SearchTerm[] terms = new SearchTerm[keywords.length];
                    for(int i=0; i<keywords.length; i++) {
                        terms[i] = new SubjectTerm(keywords[i]);
                    }
                    SearchTerm searchTerm = new OrTerm(terms);

                    Message[] messages = folder.search(searchTerm);

                    for (Message msg : messages) {
                        callback.onEmailFound(msg, folderName);
                    }

                    folder.close(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getTextFromMessage(Message message) throws Exception {
        if (message.isMimeType("text/plain")) {
            return message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            return getTextFromMimeMultipart((MimeMultipart) message.getContent());
        }
        return "";
    }

    private String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws Exception {
        StringBuilder result = new StringBuilder();
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            javax.mail.BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result.append(bodyPart.getContent());
            }
        }
        return result.toString();
    }
}