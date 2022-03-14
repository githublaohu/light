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
package com.lamp.light.route;

import java.net.InetSocketAddress;

public class DefaultRouteSelect implements RouteSelect {

	private DefaultLampInstance defaultLampInstance = new DefaultLampInstance();

	public DefaultRouteSelect(InetSocketAddress inetSocketAddress) {
		defaultLampInstance.inetSocketAddress = inetSocketAddress;
	}

	@Override
	public LampInstance select(Object[] args, Class<?> clazz) {

		return defaultLampInstance;
	}

	class DefaultLampInstance implements LampInstance {

		private InetSocketAddress inetSocketAddress;

		@Override
		public InetSocketAddress getInetSocketAddress() {

			return this.inetSocketAddress;
		}

	}
}
