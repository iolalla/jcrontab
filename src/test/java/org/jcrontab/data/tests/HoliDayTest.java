/**
 *  This file is part of the jcrontab package
 *  Copyright (C) 2001-2026 Israel Olalla
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 */
package org.jcrontab.data.tests;

import org.jcrontab.data.HoliDay;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("HoliDay Entity Tests")
public class HoliDayTest {

    @Test
    @DisplayName("Tests setting and getting HoliDay ID")
    void testSetAndGet() {
        HoliDay holiday = new HoliDay();
        int id = 42;
        holiday.setId(id);
        assertEquals(id, holiday.getId());
    }

    @Test
    @DisplayName("Tests HoliDay equals and hashCode")
    void testEqualsAndHashCode() {
        HoliDay h1 = new HoliDay();
        h1.setId(10);
        HoliDay h2 = new HoliDay();
        h2.setId(10);
        HoliDay h3 = new HoliDay();
        h3.setId(20);

        assertEquals(h1, h2);
        assertNotEquals(h1, h3);
    }

    @Test
    @DisplayName("Tests HoliDay toString")
    void testToString() {
        HoliDay holiday = new HoliDay();
        holiday.setId(5);
        assertNotNull(holiday.toString());
    }
}
