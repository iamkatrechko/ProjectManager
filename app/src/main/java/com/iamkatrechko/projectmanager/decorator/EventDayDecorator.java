package com.iamkatrechko.projectmanager.decorator;

import android.graphics.Color;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Locale;

/**
 * Декоратор дней с задачами
 * @author iamkatrechko
 *         Date: 11.06.2017
 */
public class EventDayDecorator implements DayViewDecorator {

    /** Список дней, за которые есть задачи */
    private HashSet<String> allTasksDays;

    /**
     * Конструктор
     * @param allTasksDays список дней, за которые есть задачи
     */
    public EventDayDecorator(HashSet<String> allTasksDays) {
        this.allTasksDays = allTasksDays;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        String currentDay = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(day.getDate());
        return allTasksDays.contains(currentDay);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new DotSpan(7, Color.DKGRAY));
    }
}
