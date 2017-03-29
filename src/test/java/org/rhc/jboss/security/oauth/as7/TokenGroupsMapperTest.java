package org.rhc.jboss.security.oauth.as7;

import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *  Token groups mapper test
 */
public class TokenGroupsMapperTest extends TestCase {

    @Test
    public void testMapper() {


        final String mappCfg = "AppDynamics_Server_Monitoring_User->admin;fromGroupB->toGroupB1";

        final TokenGroupsMapper mapper = TokenGroupsMapper.TokenGroupsMapperFactory.create(mappCfg);

        List<String> fromGroups1 = new ArrayList<>();
        fromGroups1.add("group1");
        fromGroups1.add("group2");
        fromGroups1.add("AppDynamics_Server_Monitoring_User");

        final List<String> result = mapper.mapGroups(fromGroups1);

        assertTrue(result.stream().filter(group->"admin".equals(group)).collect(Collectors.toList()).size() == 1);
        assertTrue(result.stream().filter(group->"group1".equals(group)).collect(Collectors.toList()).size() == 1);
        assertTrue(result.stream().filter(group->"group2".equals(group)).collect(Collectors.toList()).size() == 1);

        result.forEach(System.out::println);

    }
}
