package com.example.ProgettoAMIF.fasciaoraria.data;


public class FasciaOraria {

    public static final int TOAST = 0;
    public static final int NOTIFICATION = 1;
    public static final int DIALOG = 2;
    public static int IDpool = 0;

    private final int ID;
    private String name;
    private boolean active;
    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;
    private int secondiPermessi;
    private int notificationType;

    public int getID() {
        return ID;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
    public int getStartHour() {
        return startHour;
    }
    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }
    public int getStartMinute() {
        return startMinute;
    }
    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }
    public int getEndHour() {
        return endHour;
    }
    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }
    public int getEndMinute() {
        return endMinute;
    }
    public void setEndMinute(int endMinute) {
        this.endMinute = endMinute;
    }
    public int getSecondiPermessi() {
        return secondiPermessi;
    }
    public void setSecondiPermessi(int secondiPermessi) {
        this.secondiPermessi = secondiPermessi;
    }
    public int getNotificationType() {
        return notificationType;
    }
    public void setNotificationType(int notificationType) {
        this.notificationType = notificationType;
    }

    public static FasciaOraria newInstance(int ID, String name, boolean active, int startHour, int startMinute, int endHour, int endMinute, int secondiPermessi, int notificationType) {
        return new FasciaOraria(ID, name, active, startHour, startMinute, endHour, endMinute, secondiPermessi, notificationType);
    }

    public FasciaOraria(){
        this.ID = FasciaOraria.IDpool++;
        this.name = "Time Slot";
        this.active = false;
        this.startHour = 0;
        this.startMinute = 0;
        this.endHour = 0;
        this.endMinute = 0;
        this.secondiPermessi = 10;
        this.notificationType = FasciaOraria.TOAST;
    }

    public FasciaOraria(int ID, String name, boolean active, int startHour, int startMinute, int endHour, int endMinute, int secondiPermessi, int notificationType) {
        if(ID > FasciaOraria.IDpool){
            FasciaOraria.IDpool = ID+1;
            this.ID = ID;
        } else    this.ID = IDpool++;
        this.name = name;
        this.active = active;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
        this.secondiPermessi = secondiPermessi;
        this.notificationType = notificationType;
    }


    @Override
    public String toString() {
        return "FasciaOraria [ID=" + ID +
                            ", name=" + name +
                            ", active=" + active +
                            ", startHour=" + startHour +
                            ", startMinute=" + startMinute +
                            ", endHour=" + endHour +
                            ", endMinute=" + endMinute +
                            ", secondiPermessi=" + secondiPermessi +
                            ", notificationType=" + notificationType + "]";
    }
}
