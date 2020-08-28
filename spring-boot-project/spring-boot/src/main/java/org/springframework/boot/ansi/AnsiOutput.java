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

package org.springframework.boot.ansi;

import java.util.Locale;

import org.springframework.util.Assert;

/**
 * 生成ANSI编码的输出，自动尝试检测终端是否支持ANSI编码.
 *
 * @author Phillip Webb
 * @since 1.0.0
 */
public abstract class AnsiOutput {

	private static final String ENCODE_JOIN = ";";

	private static Enabled enabled = Enabled.DETECT;

	private static Boolean consoleAvailable;

	private static Boolean ansiCapable;

	private static final String OPERATING_SYSTEM_NAME = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);

	private static final String ENCODE_START = "\033[";

	private static final String ENCODE_END = "m";

	private static final String RESET = "0;" + AnsiColor.DEFAULT;

	/**
	 * 设置是否启用ANSI输出。
	 *
	 * @param enabled 如果启用，禁用或检测到ANSI。
	 */
	public static void setEnabled(Enabled enabled) {
		Assert.notNull(enabled, "Enabled must not be null");
		AnsiOutput.enabled = enabled;
	}

	/**
	 * 返回是否启用ANSI输出
	 *
	 * @return 如果启用，禁用或检测到ANSI
	 */
	public static Enabled getEnabled() {
		return AnsiOutput.enabled;
	}

	/**
	 * 设置是否已知System.console（）可用。
	 *
	 * @param consoleAvailable 如果已知该控制台可用，或者{@code null} 使用标准检测逻辑。
	 */
	public static void setConsoleAvailable(Boolean consoleAvailable) {
		AnsiOutput.consoleAvailable = consoleAvailable;
	}

	/**
	 * 如果启用了输出，则对单个{@link AnsiElement}进行编码。
	 *
	 * @param element 要编码的元素
	 * @return 编码的元素或空字符串
	 */
	public static String encode(AnsiElement element) {
		if (isEnabled()) {
			return ENCODE_START + element + ENCODE_END;
		}
		return "";
	}

	/**
	 * 从指定的元素创建一个新的ANSI字符串。任何{@link AnsiElement}都将根据需要进行编码。
	 *
	 * @param elements 要编码的元素
	 * @return 一串编码的元素
	 */
	public static String toString(Object... elements) {
		StringBuilder sb = new StringBuilder();
		if (isEnabled()) {
			buildEnabled(sb, elements);
		} else {
			buildDisabled(sb, elements);
		}
		return sb.toString();
	}

	private static void buildEnabled(StringBuilder sb, Object[] elements) {
		boolean writingAnsi = false;
		boolean containsEncoding = false;
		for (Object element : elements) {
			if (element instanceof AnsiElement) {
				containsEncoding = true;
				if (!writingAnsi) {
					sb.append(ENCODE_START);
					writingAnsi = true;
				} else {
					sb.append(ENCODE_JOIN);
				}
			} else {
				if (writingAnsi) {
					sb.append(ENCODE_END);
					writingAnsi = false;
				}
			}
			sb.append(element);
		}
		if (containsEncoding) {
			sb.append(writingAnsi ? ENCODE_JOIN : ENCODE_START);
			sb.append(RESET);
			sb.append(ENCODE_END);
		}
	}

	private static void buildDisabled(StringBuilder sb, Object[] elements) {
		for (Object element : elements) {
			if (!(element instanceof AnsiElement) && element != null) {
				sb.append(element);
			}
		}
	}

	private static boolean isEnabled() {
		if (enabled == Enabled.DETECT) {
			if (ansiCapable == null) {
				ansiCapable = detectIfAnsiCapable();
			}
			return ansiCapable;
		}
		return enabled == Enabled.ALWAYS;
	}

	private static boolean detectIfAnsiCapable() {
		try {
			if (Boolean.FALSE.equals(consoleAvailable)) {
				return false;
			}
			if ((consoleAvailable == null) && (System.console() == null)) {
				return false;
			}
			return !(OPERATING_SYSTEM_NAME.contains("win"));
		} catch (Throwable ex) {
			return false;
		}
	}

	/**
	 * P可能的值传递给{@link Output＃setEnabled}。确定何时输出用于着色应用程序输出的ANSI转义序列。
	 */
	public enum Enabled {

		/**
		 * 尝试检测ANSI着色功能是否可用。
		 * {@link AnsiOutput}的默认值.
		 */
		DETECT,

		/**
		 * 启用ANSI彩色输出。
		 */
		ALWAYS,

		/**
		 * 禁用ANSI彩色输出。
		 */
		NEVER

	}

}
