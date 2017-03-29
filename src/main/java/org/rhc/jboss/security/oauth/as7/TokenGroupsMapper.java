package org.rhc.jboss.security.oauth.as7;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Token groups mapper
 */
public class TokenGroupsMapper {

    private final Map<String, String> groupsMap;


    private TokenGroupsMapper(Map<String, String> groupsMap) {

        this.groupsMap = groupsMap;
    }


    /**
     * Coverts groups
     *
     * @param from
     * @return
     */
    public  List<String> mapGroups(List<String> from) {

        List<String>  newGroups =  from.stream().map(group->{

            if (groupsMap.containsKey(group)) {

                return groupsMap.get(group);
            }

            return  group;

        }).collect(Collectors.toList());

        return newGroups;
    }


    /**
     * Factory class
     */
    public static class TokenGroupsMapperFactory {

        public static TokenGroupsMapper create(final String cfgString) {

           Map<String, String> groupsMap = new ConcurrentHashMap<>();

            Stream.of(cfgString.split(";"))
                    .map(s->s.split("->"))
                    .forEach(arr-> {

                        groupsMap.put(arr[0], arr[1]);
                    });

            final TokenGroupsMapper mapper = new TokenGroupsMapper(groupsMap);

            return  mapper;
        }
    }
}
