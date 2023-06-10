package com.bot.projects.model;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Date;
import java.util.TimeZone;

@Component
@RequestScope
public class CurrentSession {
    Date timeZoneNow;
    UserDetail userDetail;
    TimeZone timeZone;
    public Date getTimeZoneNow() {
        return timeZoneNow;
    }

    public void setTimeZoneNow(Date timeZoneNow) {
        this.timeZoneNow = timeZoneNow;
    }

    public UserDetail getUserDetail() {
        return userDetail;
    }

    public void setUserDetail(UserDetail userDetail) {
        this.userDetail = userDetail;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }



}
