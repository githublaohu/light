package com.lamp.light.cloud.implement;

import java.util.Map;

public class SimpleCloudImplementObject {

    private String httpMethod;

    private String path;

    private boolean isJson;

    private String serviceUrl;

    private Map<String, String> querys;

    private Map<String, String> header;

    private Map<String, String> form;

    private Object body;


    public static SimpleCloudImplementObjectBuilder createBuilder() {
        return new SimpleCloudImplementObjectBuilder();
    }

    public String getHttpMethod() {
        return httpMethod;
    }


    public String getPath() {
        return path;
    }

    public boolean isJson() {
        return isJson;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public Map<String, String> getQuerys() {
        return querys;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public Map<String, String> getForm() {
        return form;
    }

    public Object getBody() {
        return body;
    }


    public static class SimpleCloudImplementObjectBuilder {

        private String httpMethod;

        private String path;

        private boolean isJson;

        private String serviceUrl;

        private Map<String, String> querys;

        private Map<String, String> header;

        private Map<String, String> form;

        private Object body;

        public SimpleCloudImplementObjectBuilder httpMehtod() {
            return this;
        }

        public SimpleCloudImplementObjectBuilder path() {
            return this;
        }

        public SimpleCloudImplementObjectBuilder isJson() {
            return this;
        }

        public SimpleCloudImplementObjectBuilder serviceUrl() {
            return this;
        }

        public SimpleCloudImplementObjectBuilder querys() {
            return this;
        }

        public SimpleCloudImplementObjectBuilder header() {
            return this;
        }

        public SimpleCloudImplementObjectBuilder form() {
            return this;
        }

        public SimpleCloudImplementObjectBuilder body() {
            return this;
        }

        public SimpleCloudImplementObject build() {
            SimpleCloudImplementObject simpleCloudImplementObject = new SimpleCloudImplementObject();

            return simpleCloudImplementObject;
        }
    }
}
