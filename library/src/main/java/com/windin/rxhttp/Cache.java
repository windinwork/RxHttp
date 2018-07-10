package com.windin.rxhttp;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okhttp3.internal.cache.DiskLruCache;
import okhttp3.internal.io.FileSystem;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ByteString;
import okio.ForwardingSource;
import okio.Okio;

/**
 * author: windin
 * created on: 18-6-28 下午3:38
 * email: windinwork@gmail.com
 */
public class Cache {
    private static final int VERSION = 201806;
    private static final int ENTRY_METADATA = 0;
    private static final int ENTRY_BODY = 1;
    private static final int ENTRY_COUNT = 2;

    private DiskLruCache cache;

    public Cache(File directory, long maxSize) {
        this(directory, maxSize, FileSystem.SYSTEM);
    }

    private Cache(File directory, long maxSize, FileSystem fileSystem) {
        this.cache = DiskLruCache.create(fileSystem, directory, VERSION, ENTRY_COUNT, maxSize);
    }

    public ResponseBody get(String key) {
        try {
            String safeKey = safeKey(key);
            ;
            final DiskLruCache.Snapshot snapshot = cache.get(safeKey);

            if (snapshot == null) {
                return null;
            }

            BufferedSource source = Okio.buffer(snapshot.getSource(0));
            String mediaType = source.readUtf8LineStrict();
            long contentLength = source.readLong();
            source.close();

            final BufferedSource contentSource = Okio.buffer(snapshot.getSource(1));
            BufferedSource bodySource = Okio.buffer(new ForwardingSource(contentSource) {
                @Override
                public void close() throws IOException {
                    snapshot.close();
                    super.close();
                }
            });

            return ResponseBody.create(MediaType.parse(mediaType), contentLength, bodySource);

        } catch (IOException e) {
            // TODO: 18-6-28
            e.printStackTrace();
        }

        return null;
    }

    public void put(String key, ResponseBody body) {
        String safeKey = safeKey(key);

        String mediaType = body.contentType().toString();
        long contentLength = body.contentLength();
        BufferedSource source = body.source();

        DiskLruCache.Editor editor = null;
        try {
            editor = cache.edit(safeKey);
            BufferedSink sink = Okio.buffer(editor.newSink(ENTRY_METADATA));
            sink.writeUtf8(mediaType);
            sink.writeByte('\n');
            sink.writeLong(contentLength);
            sink.writeByte('\n');
            sink.close();

            BufferedSink contentSink = Okio.buffer(editor.newSink(ENTRY_BODY));
            contentSink.write(source, contentLength);
            contentSink.writeByte('\n');
            contentSink.close();

            editor.commit();

        } catch (IOException e) {
            // TODO: 18-6-28
            e.printStackTrace();
        }
    }

    private String safeKey(String key) {
        return ByteString.encodeUtf8(key).md5().hex();
    }
}
