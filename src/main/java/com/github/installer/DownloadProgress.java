package com.github.installer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.client.methods.AsyncByteConsumer;
import org.apache.http.protocol.HttpContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DownloadProgress extends AsyncByteConsumer<InputStream> {
	private long size;
	private long received;
	private long lastPrinted;
	private String fileName;
	private ByteArrayOutputStream out;

	public DownloadProgress(String fileName) {
		this.size = 0;
		this.received = 0;
		this.lastPrinted = 0;
		this.fileName = fileName;
		this.out = new ByteArrayOutputStream();
	}

	private void printProgress() {
		if (this.size <= 0) {
			log.debug("{} : received {} Bytes", this.fileName, this.received);
		} else {
			double percentage = Math.round((10000.0 * this.received / this.size)) / 100.0;
			log.debug("{} : received {} Bytes ({}%)", this.fileName, this.received, percentage);
		}
		this.lastPrinted = this.received;
	}

	@Override
	protected void onByteReceived(ByteBuffer buf, IOControl ioctrl) throws IOException {
		while (buf.hasRemaining()) {
			out.write(buf.get());
			received++;
		}

		boolean shouldPrint = false;
		if (this.size <= 0) {
			// illegal total size, print every 1KiB
			if (received - lastPrinted > 1024) {
				shouldPrint = true;
			}
		} else {
			// calculate percentage of downloaded, print every 10%
			double tenPercent = 0.1 * this.size;
			if (received - lastPrinted > tenPercent) {
				shouldPrint = true;
			}
		}

		if (shouldPrint) {
			printProgress();
		}
	}

	@Override
	protected void onResponseReceived(HttpResponse response) throws HttpException, IOException {
		Header cl = response.getFirstHeader("Content-Length");
		if (cl != null) {
			String clText = cl.getValue();
			try {
				this.size = Long.parseLong(clText);
			} catch (Exception ex) {
				this.size = 0;
			}
		}
	}

	@Override
	protected InputStream buildResult(HttpContext context) throws Exception {
		return new ByteArrayInputStream(this.out.toByteArray());
	}

}
