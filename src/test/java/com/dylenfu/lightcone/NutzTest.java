/*

  Copyright 2017 Loopring Project Ltd (Loopring Foundation).

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.

*/

package com.dylenfu.lightcone;

import com.dylenfu.lightcone.persistence.Person;
import com.google.inject.Injector;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.nutz.dao.*;
import org.nutz.dao.util.Daos;

import java.util.List;

import static org.nutz.json.JsonFormat.Function.actived;

public class NutzTest {

    @Test
    public void insertTest() {
        Injector injector = Common.getInjector();
        Dao dao = injector.getInstance(Dao.class);
        Logger logger = injector.getInstance(Logger.class);

        Person person = new Person();
        person.setName("zhizhi");
        person.setAge(31);
        dao.insert(person);
        logger.debug("person id " + person.getId());
    }

    @Test
    public void getTest() {
        Injector injector = Common.getInjector();
        Dao dao = injector.getInstance(Dao.class);
        Logger logger = injector.getInstance(Logger.class);

        Person tom = dao.fetch(Person.class, "tom");
        logger.debug("tom " + tom.toString());

        Person jane = dao.fetch(Person.class, 7);
        logger.debug("jane " + jane.toString());
    }

    @Test
    public void conditionTest() {
        Injector injector = Common.getInjector();
        Dao dao = injector.getInstance(Dao.class);
        Logger logger = injector.getInstance(Logger.class);

        List<Person> list1 = dao.query(Person.class, Cnd.where("id", ">", 0).where("name", "like", "%j%"));
        for(Person person: list1) {
            logger.debug(person.toString());
        }

        // 注意: 这里limit pageNumber从1开始，0代表取所有数据，官方已放弃limit(n)的写法，但该jar包仍然可以使用
        // For example:> "SELECT * FROM lpr_person  WHERE age>10 AND id IN (4,7,8,9,11,12,13) ORDER BY id DESC   LIMIT 3, 3 "
        int[] ids = {4, 7, 8, 9, 11, 12, 13};
        Condition cnd = Cnd.where("age", ">", 10).and("id", "in", ids).limit(2, 3).desc("id");
        List<Person> list2 = dao.query(Person.class, cnd);
        for (Person person: list2) {
            logger.debug(person.toString());
        }
    }

    @Test
    public void updateObjectTest() {
        Injector injector = Common.getInjector();
        Dao dao = injector.getInstance(Dao.class);
        Logger logger = injector.getInstance(Logger.class);

        // For example:> "UPDATE lpr_person SET name='dylenfu',age=32  WHERE id=4"
        Person person = dao.fetch(Person.class, 4);
        person.setAge(32);
        int affectedRows = dao.update(person);
        logger.debug("person age " + person.getAge() + " affected rows " + affectedRows);
    }

    @Test
    public void updateFieldTest() {
        Injector injector = Common.getInjector();
        Dao dao = injector.getInstance(Dao.class);
        Logger logger = injector.getInstance(Logger.class);

        // For example:> "UPDATE lpr_person SET age=31  WHERE id=4"
        Person person = dao.fetch(Person.class, 4);
        person.setAge(31);
        int affectedRows = dao.update(person, "^age$");
        logger.debug("person age " + person.getAge() + " affected rows " + affectedRows);
    }

    @Test
    public void fieldFilterTest() {
        Injector injector = Common.getInjector();
        Dao dao = injector.getInstance(Dao.class);
        Logger logger = injector.getInstance(Logger.class);

        // For example:> "UPDATE lpr_person SET age=31  WHERE id=4"
        Person person = dao.fetch(Person.class, 4);
        person.setName("dylenfu");
        person.setAge(31);

        // ignore null, only fields of name and age updated
        int affectedRows = Daos.ext(dao, FieldFilter.create(Person.class, "^name|age$")).update(person);
        logger.debug("person age " + person.getAge() + " affected rows " + affectedRows);
    }
}
