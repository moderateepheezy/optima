package ru.max64.myappstime.model;

import android.graphics.drawable.Drawable;

public class StatEntry {

    private Drawable icon;
    private String title;
    private int time;
    private long installDate;
    private long lastUpdated;
    private String packageName;
    private boolean systemApp;
    private String cpuUsage;

    public StatEntry() {
    }

    public StatEntry(Drawable icon, String title, long installDate, int time) {
        this.icon = icon;
        this.title = title;
        this.installDate = installDate;
        this.time = time;
    }


    public String getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(String cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    @Override
    public String toString() {
        return "StatEntry [title=" + title + ", time=" + time + ", packageName=" + packageName
                + ", systemApp=" + systemApp + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((packageName == null) ? 0 : packageName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        StatEntry other = (StatEntry) obj;
        if (packageName == null) {
            if (other.packageName != null)
                return false;
        } else if (!packageName.equals(other.packageName))
            return false;
        return true;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public long getInstallDate() {
        return installDate;
    }

    public void setInstallDate(long installDate) {
        this.installDate = installDate;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean isSystemApp() {
        return systemApp;
    }

    public void setSystemApp(boolean systemApp) {
        this.systemApp = systemApp;
    }

}
