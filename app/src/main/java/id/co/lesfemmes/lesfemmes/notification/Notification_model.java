package id.co.lesfemmes.lesfemmes.notification;

public class Notification_model {

    String CustomerCode,NotificationDate,NotificationTitle,NotificationMessage;
    Integer NotificationStatus,NotificationId,NotificationType;

    public Notification_model(String customerCode, String notificationDate, String notificationTitle, String notificationMessage, Integer notificationStatus,Integer notificationId,Integer NotificationType) {
        this.CustomerCode = customerCode;
        this.NotificationDate = notificationDate;
        this.NotificationTitle = notificationTitle;
        this.NotificationMessage = notificationMessage;
        this.NotificationStatus = notificationStatus;
        this.NotificationId = notificationId;
        this.NotificationType = NotificationType;
    }

    public Integer getNotificationType() {
        return NotificationType;
    }

    public void setNotificationType(Integer notificationType) {
        NotificationType = notificationType;
    }

    public String getCustomerCode() {
        return CustomerCode;
    }

    public void setCustomerCode(String customerCode) {
        CustomerCode = customerCode;
    }

    public String getNotificationDate() {
        return NotificationDate;
    }

    public void setNotificationDate(String notificationDate) {
        NotificationDate = notificationDate;
    }

    public String getNotificationTitle() {
        return NotificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        NotificationTitle = notificationTitle;
    }

    public String getNotificationMessage() {
        return NotificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        NotificationMessage = notificationMessage;
    }

    public Integer getNotificationStatus() {
        return NotificationStatus;
    }

    public void setNotificationStatus(Integer notificationStatus) {
        NotificationStatus = notificationStatus;
    }

    public Integer getNotificationId() {
        return NotificationId;
    }

    public void setNotificationId(Integer notificationId) {
        NotificationId = notificationId;
    }
}
