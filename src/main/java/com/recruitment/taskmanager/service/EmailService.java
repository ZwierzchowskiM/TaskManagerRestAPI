package com.recruitment.taskmanager.service;

import com.recruitment.taskmanager.model.Email;
import com.recruitment.taskmanager.model.Task;
import com.recruitment.taskmanager.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmailService  {

    @Value("${spring.mail.username}") private String sender;
    private JavaMailSender emailSender;

    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendMail(Email details)
    {
            SimpleMailMessage mailMessage
                    = new SimpleMailMessage();

            mailMessage.setFrom(sender);
            mailMessage.setTo(details.getRecipient());
            mailMessage.setText(details.getMsgBody());
            mailMessage.setSubject(details.getSubject());

            emailSender.send(mailMessage);
        }


    public void sendUserAddedToTask(User user, Task task)
    {
        Email email = createEmailAddedToTask(user, task);
        sendMail(email);
    }

    public void sendUserRemovedFromTask(User user, Task task)
    {
        Email email = createEmailRemovedFromTask(user, task);
        sendMail(email);
    }

    public void sendToAllTaskUsers (Task task)
    {
        List<User> users = task.getUsers();
        users.forEach(user -> sendUserAddedToTask(user, task));

    }

    private Email createEmailAddedToTask (User user, Task task) {
        Email email = new Email();
        email.setRecipient(user.getEmail());
        email.setSubject("Added to task: " + task.getTitle());
        String msgBody = "Hello "+ user.getFirstName()+"! \n" + "You have been added to task: " + task.getTitle()+ ". " +
                "Task description: "+ task.getDescription() + " \n" +
                "Due date for this task is: " + task.getDueDate();
        email.setMsgBody(msgBody);

        return email;
    }

    private Email createEmailRemovedFromTask (User user, Task task) {
        Email email = new Email();
        email.setRecipient(user.getEmail());
        email.setSubject("Removed from task: " + task.getTitle());
        String msgBody = "Hello "+ user.getFirstName()+"! \n" +" You have been removed from task: " + task.getTitle();
        email.setMsgBody(msgBody);

        return email;
    }


}
