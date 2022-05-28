/*
 *Copyright (c) [Year] [name of copyright holder]
 *[Software Name] is licensed under Mulan PubL v2.
 *You can use this software according to the terms and conditions of the Mulan PubL v2.
 *You may obtain a copy of Mulan PubL v2 at:
 *         http://license.coscl.org.cn/MulanPubL-2.0
 *THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 *EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 *MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *See the Mulan PubL v2 for more details.
 */
package com.lamp.light.handler;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.lamp.light.MultipartUpload;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.QueryStringEncoder;
import io.netty.handler.codec.http.multipart.HttpPostRequestEncoder;
import io.netty.handler.codec.http.multipart.HttpPostRequestEncoder.ErrorDataEncoderException;
import io.netty.handler.codec.http.multipart.MixedFileUpload;

public interface CoordinateHandler<T, V> {

	static final ThreadLocal<CoordinateHandlerWrapper> COORDINATEHANDLER = new ThreadLocal<CoordinateHandlerWrapper>() {

		public CoordinateHandlerWrapper initialValue() {
			return new CoordinateHandlerWrapper();
		}
	};

	public static CoordinateHandlerWrapper getCoordinateHandlerWrapper() {
		return COORDINATEHANDLER.get();
	}

	void handler(String key, V value);

	void clean();

	static abstract class AbstractCoordinateHandler<T, V> implements CoordinateHandler<T, V> {

		T object;

		void setObject(T object) {
			this.object = object;
		}

		public void clean() {
			this.object = null;
		}
	}

	static class CookieCoordinateHandler extends AbstractCoordinateHandler<HttpHeaders, String> {
		@Override
		public void handler(String name, String value) {
			String string = object.get(HttpHeaderNames.COOKIE);
			if (Objects.isNull(string)) {
				string = "";
			}
			string = string + ":\"" + value + "\";";
			object.add(name, string);
		}
	}

	static class HeaderCoordinateHandler extends AbstractCoordinateHandler<HttpHeaders, String> {
		@Override
		public void handler(String name, String value) {
			object.add(name, value);
		}
	}

	static class PathCoordinateHandler extends AbstractCoordinateHandler<Map<String, String>, String> {
		@Override
		public void handler(String name, String value) {
			this.object.put(name, value);
		}
	}

	static class QueryCoordinateHandler extends AbstractCoordinateHandler<QueryStringEncoder, String> {
		@Override
		public void handler(String name, String value) {
			object.addParam(name, value);
		}
	}

	static class FieldCoordinateHandler extends AbstractCoordinateHandler<HttpPostRequestEncoder, String> {
		@Override
		public void handler(String name, String value) {
			try {
				object.addBodyAttribute(name, value);
			} catch (ErrorDataEncoderException e) {
				throw new RuntimeException(e);
			}
		}
	}

	static class UploadCoordinateHandler extends AbstractCoordinateHandler<HttpPostRequestEncoder, Object> {
		@SuppressWarnings("unchecked")
		@Override
		public void handler(String name, Object value) {
			try {
				if (value instanceof File) {

				} else if (value instanceof MultipartUpload) {
					this.addBodyHttpData((MultipartUpload) value);
				}

				if (value instanceof List) {
					List<MultipartUpload> multipartUploadList = (List<MultipartUpload>) value;
					for (MultipartUpload multipartUpload : multipartUploadList) {
						this.addBodyHttpData(multipartUpload);
					}
				}

			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		private void addBodyHttpData(MultipartUpload multipartUpload) throws Exception {

			MixedFileUpload fileUpload = new MixedFileUpload(multipartUpload.name(), multipartUpload.fileName(),
					multipartUpload.contentType(), null, multipartUpload.charset(), multipartUpload.size(),
					1024 * 1024 * 1024);
			if (Objects.nonNull(multipartUpload.updateFile())) {
				fileUpload.setContent(multipartUpload.updateFile());
			} else {
				fileUpload.setContent(multipartUpload.uploadStream());
			}
			object.addBodyHttpData(fileUpload);
		}
	}

	public static class CoordinateHandlerWrapper {

		public QueryCoordinateHandler queryCoordinateHandler = new QueryCoordinateHandler();

		public FieldCoordinateHandler fieldCoordinateHandler = new FieldCoordinateHandler();

		public PathCoordinateHandler pathCoordinateHandler = new PathCoordinateHandler();

		public HeaderCoordinateHandler headerCoordinateHandler = new HeaderCoordinateHandler();

		public CookieCoordinateHandler cookieCoordinateHandler = new CookieCoordinateHandler();

		public UploadCoordinateHandler uploadCoordinateHandler = new UploadCoordinateHandler();

	}
}
