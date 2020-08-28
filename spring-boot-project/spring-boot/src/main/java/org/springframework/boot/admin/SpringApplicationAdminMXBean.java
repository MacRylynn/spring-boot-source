/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.admin;

/**
 * 通过JMX控制和监视正在运行的{@code SpringApplication}的MBean.
 * 仅供内部使用.
 *
 * @author Stephane Nicoll
 * @since 1.3.0
 */
public interface SpringApplicationAdminMXBean {

	/**
	 * 指定应用程序是否已完全启动并且现在已经准备就绪。
	 *
	 * @return {@code true} 如果应用程序准备好了。
	 * @see org.springframework.boot.context.event.ApplicationReadyEvent
	 */
	boolean isReady();

	/**
	 * 指定应用程序是否在内嵌的Web容器中运行。 Return {@code false}
	 * 如果返回false表示在尚未完全启动的Web应用程序上运行，因此最好等待应用程序准备就绪{@link #isReady（）}。
	 *
	 * @return {@code true} 如果返回true表示应用程序在内嵌的Web容器中运行。
	 * @see #isReady()
	 */
	boolean isEmbeddedWebApplication();

	/**
	 * 从应用程序返回指定键的值。
	 * {@link org.springframework.core.env.Environment Environment}.
	 *
	 * @param key 属性键。
	 * @return 属性值或{@code null}（如果不存在）。
	 */
	String getProperty(String key);

	/**
	 * 关闭应用程序。
	 *
	 * @see org.springframework.context.ConfigurableApplicationContext#close()
	 */
	void shutdown();

}
