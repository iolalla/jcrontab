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
import org.jcrontab.data.CrontabEntryBean;
import org.jcrontab.data.CrontabEntryDAO;
import org.jcrontab.data.CrontabParser;
import org.jcrontab.data.DataNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CrontabEntryDAO Operations Tests")
public class DAOTest {

    private final CrontabParser cp = new CrontabParser();
    private final CrontabEntryBean[] ceb = new CrontabEntryBean[3];

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
        ceb[0].setBusinessDays(false);
        ceb[0].setId(0);

        ceb[1] = cp.marshall("* * * * * org.jcrontab.tests.test testing 2");
        ceb[1].setYears("*");
        ceb[1].setSeconds("0");
        ceb[1].setBusinessDays(false);
        ceb[1].setId(1);

        ceb[2] = cp.marshall("* * * * * org.jcrontab.tests.test testing 3");
        ceb[2].setYears("*");
        ceb[2].setSeconds("0");
        ceb[2].setBusinessDays(false);
        ceb[2].setId(2);

        CrontabEntryDAO.getInstance().store(ceb);
    }

    @AfterEach
    void tearDown() throws Exception {
        try {
            CrontabEntryDAO instance = CrontabEntryDAO.getInstance();
            CrontabEntryBean[] findAll = instance.findAll();
            if (findAll != null && findAll.length > 0) {
                instance.remove(findAll);
            }
        } catch (DataNotFoundException ignored) {}
    }

    @Test
    @DisplayName("Finds all stored beans")
    void testFindAll() throws Exception {
        CrontabEntryBean[] ceb2 = CrontabEntryDAO.getInstance().findAll();
        assertNotNull(ceb2);
        assertEquals(3, ceb2.length);
    }

    @Test
    @DisplayName("Finds specific entry bean")
    void testFindSpecific() throws Exception {
        CrontabEntryBean ceb2 = CrontabEntryDAO.getInstance().find(ceb[0]);
        assertNotNull(ceb2);
        assertEquals(ceb[0], ceb2);
    }

    @Test
    @DisplayName("Removes entries from DAO")
    void testRemove() throws Exception {
        CrontabEntryBean[] toRemove = {ceb[0], ceb[1]};
        CrontabEntryDAO.getInstance().remove(toRemove);

        CrontabEntryBean[] remaining = CrontabEntryDAO.getInstance().findAll();
        assertNotNull(remaining);
        assertEquals(1, remaining.length);
    }
}
