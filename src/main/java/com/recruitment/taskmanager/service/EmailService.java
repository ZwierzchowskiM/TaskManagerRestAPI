package com.recruitment.taskmanager.service;

import com.recruitment.taskmanager.model.Email;
import com.recruitment.taskmanager.model.Task;
import com.recruitment.taskmanager.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EmailService  {

    @Value("${spring.mail.username}") private String sender;

    private JavaMailSender emailSender;

    private final TemplateEngine templateEngine;

    public EmailService(JavaMailSender emailSender, TemplateEngine templateEngine) {
        this.emailSender = emailSender;
        this.templateEngine = templateEngine;
    }

    public void sendMail(Email email) throws MessagingException {

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        Context context = new Context();
        context.setVariables(email.getProperties());

        helper.setFrom(email.getFrom());
        helper.setTo(email.getTo());
        helper.setSubject(email.getSubject());
        String html = templateEngine.process(email.getTemplate(), context);
        helper.setText(html, true);

        emailSender.send(message);
        }


    public void sendUserAddedToTask(User user, Task task)
    {
        Email email = createEmailAddedToTask(user, task);
        try {
            sendMail(email);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }

    public void sendUserRemovedFromTask(User user, Task task)
    {
        Email email = createEmailRemovedFromTask(user, task);
        try {
            sendMail(email);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendToAllTaskUsers (Task task)
    {
        List<User> users = task.getUsers();
        users.forEach(user -> sendUserAddedToTask(user, task));

    }

    private Email createEmailAddedToTask (User user, Task task) {

        Email email = new Email();
        email.setTo(user.getEmail());
        email.setFrom(sender);
        email.setSubject("Added to task");
        email.setTemplate("added-to-task-template.html");

        Map<String, Object> properties = new HashMap<>();
        properties.put("name", user.getFirstName());
        properties.put("title", task.getTitle());
        properties.put("description", task.getDescription());
        properties.put("dueDate", task.getDueDate());
        properties.put("users", task.getUsers().stream().map(User::toString).toArray());
        email.setProperties(properties);

        return email;
    }

    private Email createEmailRemovedFromTask (User user, Task task) {
        Email email = new Email();

        email.setTo(user.getEmail());
        email.setFrom(sender);
        email.setSubject("Removed from task");
        email.setTemplate("removed-from-task-template.html");

        Map<String, Object> properties = new HashMap<>();
        properties.put("name", user.getFirstName());
        properties.put("title", task.getTitle());
        email.setProperties(properties);

        return email;
    }

}
