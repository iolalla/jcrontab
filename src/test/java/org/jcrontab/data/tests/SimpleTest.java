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

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;
import org.jcrontab.Crontab;
import org.jcrontab.data.CalendarBuilder;
import org.jcrontab.data.CrontabEntryBean;
import org.jcrontab.data.CrontabEntryDAO;
import org.jcrontab.data.CrontabParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Legacy Simple DAO & CrontabParser Integration Tests")
public class SimpleTest {

    private CrontabParser cp = new CrontabParser();
    private CrontabEntryBean[] ceb = new CrontabEntryBean[3];

    @BeforeEach
    void setUp() throws Exception {
        File dir = new File("target/test-classes/.jcrontab");
        dir.mkdirs();
        File crontabFile = new File(dir, "crontab");
        if (crontabFile.exists()) {
            crontabFile.delete();
        }
        crontabFile.createNewFile();

        Crontab crontab = Crontab.getInstance();
        Properties props = new Properties();

        try (InputStream in = getClass().getClassLoader().getResourceAsStream("jcrontab.test.properties");
             Reader inStream = new InputStreamReader(in)) {
            props.load(inStream);
        }
        crontab.init(props);

        ceb[0] = cp.marshall("* * * * * org.jcrontab.tests.test testing");
        ceb[0].setYears("*");
        ceb[0].setSeconds("0");
        ceb[0].setBusinessDays(true);
        ceb[0].setId(0);

        ceb[1] = cp.marshall("* * * * * org.jcrontab.tests.test testing 2");
        ceb[1].setYears("*");
        ceb[1].setSeconds("0");
        ceb[1].setBusinessDays(true);
        ceb[1].setId(1);

        ceb[2] = cp.marshall("* * * * * org.jcrontab.tests.test testing 3");
        ceb[2].setYears("*");
        ceb[2].setSeconds("0");
        ceb[2].setBusinessDays(true);
        ceb[2].setId(2);

        CrontabEntryDAO.getInstance().store(ceb);
    }

    @AfterEach
    void tearDown() throws Exception {
        CrontabEntryDAO instance = CrontabEntryDAO.getInstance();
        CrontabEntryBean[] findAll = instance.findAll();
        if (findAll != null && findAll.length > 0) {
            instance.remove(findAll);
        }
    }

    @Test
    @DisplayName("Stores beans into DAO")
    void testDAOAdd() throws Exception {
        CrontabEntryDAO.getInstance().store(ceb);
    }

    @Test
    @DisplayName("Finds all stored beans from DAO")
    void testDAOFindAll() throws Exception {
        CrontabEntryBean[] listOfBeans = CrontabEntryDAO.getInstance().findAll();
        assertNotNull(listOfBeans);
        assertEquals(3, listOfBeans.length);
    }

    @Test
    @DisplayName("Calculates next bean to execute")
    void testNextBeanToExecute() throws Exception {
        CrontabEntryBean[] listOfBeans = CrontabEntryDAO.getInstance().findAll();
        CalendarBuilder calb = new CalendarBuilder();
        CrontabEntryBean nextb = calb.getNextCrontabEntry(listOfBeans);
        assertNotNull(nextb);
    }

    @Test
    @DisplayName("Parses standard entry string with CrontabParser")
    void testCrontabParser() throws Exception {
        CrontabParser parser = new CrontabParser();
        CrontabEntryBean bean = parser.marshall("* * * * * org.jcrontab.tests.TaskTest");
        assertNotNull(bean);
        assertEquals("org.jcrontab.tests.TaskTest", bean.getClassName());
    }
}
