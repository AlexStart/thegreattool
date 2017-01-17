package com.sam.jcc.cloud.persistence.data;

import com.sam.jcc.cloud.exception.InternalCloudException;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.Blob;
import java.sql.SQLException;

/**
 * @author Alexey Zhytnik
 * @since 17.01.2017
 */
final class BlobUtil {

    static final Blob EMPTY = blob(new byte[0]);

    private BlobUtil() {
    }

    public static Blob blob(byte[] data) {
        try {
            return new SerialBlob(data);
        } catch (SQLException e) {
            throw new InternalCloudException(e);
        }
    }

    public static byte[] bytes(Blob blob) {
        try {
            final int length = (int) blob.length();
            if (length == 0) {
                return null;
            }
            return blob.getBytes(1, length);
        } catch (SQLException e) {
            throw new InternalCloudException(e);
        }
    }
}
