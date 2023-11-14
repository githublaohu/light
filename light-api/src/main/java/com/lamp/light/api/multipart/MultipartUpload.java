package com.lamp.light.api.multipart;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Objects;

public class MultipartUpload {

    private String fileName;

    private String contentType;

    private Charset charset;

    private long size;

    private InputStream uploadStream;

    private File updateFile;

    public String fileName() {
        return this.fileName;
    }

    public String contentType() {
        return this.contentType;
    }

    public InputStream uploadStream() {
        return this.uploadStream;
    }

    public File updateFile() {
        return this.updateFile;
    }

    public Charset charset() {
        return this.charset;
    }

    public long size() {
        return this.size;
    }

    public static MultipartUploadBuilder Builder() {
        return new MultipartUploadBuilder();
    }

    public static class MultipartUploadBuilder {

        private String fileName;

        private String contentType;

        private String uploadString;

        private byte[] uploadByte;

        private InputStream uploadStream;

        private String uploadUrl;

        private File updateFile;

        private Charset charset;

        public MultipartUploadBuilder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public MultipartUploadBuilder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public MultipartUploadBuilder uploadString(String uploadString) {
            this.uploadString = uploadString;
            return this;
        }

        public MultipartUploadBuilder uploadByte(byte[] uploadByte) {
            this.uploadByte = uploadByte;
            return this;
        }

        public MultipartUploadBuilder uploadStream(InputStream uploadStream) {
            this.uploadStream = uploadStream;
            return this;
        }

        public MultipartUploadBuilder uploadUrl(String uploadUrl) {
            this.uploadUrl = uploadUrl;
            return this;
        }

        public MultipartUploadBuilder updateFile(File file) {
            this.updateFile = file;
            return this;
        }

        public MultipartUploadBuilder charset(Charset charset) {
            this.charset = charset;
            return this;
        }

        public MultipartUpload build() {
            MultipartUpload multipartUpload = new MultipartUpload();

            if (Objects.nonNull(uploadUrl)) {
                updateFile = new File(uploadUrl);
            }
            if (Objects.nonNull(this.updateFile)) {
                String name = updateFile.getName();
                this.fileName = name.substring(0, name.lastIndexOf('.'));
                this.contentType = name.substring(name.lastIndexOf('.'));
                multipartUpload.size = updateFile.length();
                multipartUpload.updateFile = this.updateFile;
            } else {
                if (Objects.nonNull(uploadString)) {
                    this.uploadByte = this.uploadString.getBytes();
                    uploadStream = new ByteArrayInputStream(uploadByte);
                    multipartUpload.size = this.uploadByte.length;
                }
                if (Objects.nonNull(uploadByte)) {
                    uploadStream = new ByteArrayInputStream(this.uploadByte);
                    multipartUpload.size = this.uploadByte.length;
                }
                multipartUpload.uploadStream = uploadStream;
            }
            multipartUpload.fileName = fileName;
            multipartUpload.contentType = contentType;
            multipartUpload.charset = charset;
            return multipartUpload;
        }
    }
}
