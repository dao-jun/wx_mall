package com.dyf.core.utils

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

class JacksonUtil {

     static String parseString(String body, String field) {
        ObjectMapper mapper = new ObjectMapper()
        JsonNode node = null
        try {
            node = mapper.readTree(body)
            JsonNode leaf = node.get(field)
            if(leaf != null)
                return leaf.asText()
        } catch (IOException e) {
            e.printStackTrace()
        }
        return null
    }

     static Integer parseInteger(String body, String field) {
        ObjectMapper mapper = new ObjectMapper()
        JsonNode node = null
        try {
            node = mapper.readTree(body)
            JsonNode leaf = node.get(field)
            if(leaf != null)
                return leaf.asInt()
        } catch (IOException e) {
            e.printStackTrace()
        }
        return null
    }

     static List<Integer> parseIntegerList(String body, String field) {
        ObjectMapper mapper = new ObjectMapper()
        JsonNode node = null
        try {
            node = mapper.readTree(body)
            JsonNode leaf = node.get(field)

            if(leaf != null)
                return mapper.convertValue(leaf, new TypeReference<List<String>>(){})
        } catch (IOException e) {
            e.printStackTrace()
        }
        return null
    }



     static Boolean parseBoolean(String body, String field) {
        ObjectMapper mapper = new ObjectMapper()
        JsonNode node = null
        try {
            node = mapper.readTree(body)
            JsonNode leaf = node.get(field)
            if(leaf != null)
                return leaf.asBoolean()
        } catch (IOException e) {
            e.printStackTrace()
        }
        return null
    }

     static Short parseShort(String body, String field) {
        ObjectMapper mapper = new ObjectMapper()
        JsonNode node = null
        try {
            node = mapper.readTree(body)
            JsonNode leaf = node.get(field)
            if(leaf != null) {
                Integer value = leaf.asInt()
                return value.shortValue()
            }
        } catch (IOException e) {
            e.printStackTrace()
        }
        return null
    }

     static <T> T parseObject(String body, String field, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper()
        JsonNode node = null
        try {
            node = mapper.readTree(body)
            node = node.get(field)
            return mapper.treeToValue(node, clazz)
        } catch (IOException e) {
            e.printStackTrace()
        }
        return null
    }

     static Object toNode(String json) {
        if(json == null){
            return null
        }
        ObjectMapper mapper = new ObjectMapper()
        try {
            JsonNode jsonNode = mapper.readTree(json)
            return jsonNode
        } catch (IOException e) {
            e.printStackTrace()
        }

        return null
    }
}
