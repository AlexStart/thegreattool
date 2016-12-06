package com.sam.jcc.cloud.utils.parsers;

import java.util.Map.Entry;

/**
 * @author Alexey Zhytnik
 * @since 28-Nov-16
 */
interface IParser<T> {

    Entry<String, String> parse(T document);
}
