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

import java.util.Objects;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.QueryStringEncoder;
import io.netty.handler.codec.http.multipart.HttpPostRequestEncoder;
import io.netty.handler.codec.http.multipart.HttpPostRequestEncoder.ErrorDataEncoderException;

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
			string = string +":\""+ value + "\";";
			object.add(name, string);
		}
	}

	static class HeaderCoordinateHandler extends AbstractCoordinateHandler<HttpHeaders, String> {
		@Override
		public void handler(String name, String value) {
			object.add(name, value);
		}
	}

	static class PathCoordinateHandler extends AbstractCoordinateHandler<String, String> {
		@Override
		public void handler(String name, String value) {

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
		@Override
		public void handler(String name, Object value) {
			try {
				object.addBodyAttribute(name, value.toString());
			} catch (ErrorDataEncoderException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static class CoordinateHandlerWrapper {

		public QueryCoordinateHandler queryCoordinateHandler = new QueryCoordinateHandler();

		public FieldCoordinateHandler fieldCoordinateHandler = new FieldCoordinateHandler();

		public PathCoordinateHandler pathCoordinateHandler = new PathCoordinateHandler();

		public HeaderCoordinateHandler headerCoordinateHandler = new HeaderCoordinateHandler();

		public CookieCoordinateHandler cookieCoordinateHandler = new CookieCoordinateHandler();
	}
}
