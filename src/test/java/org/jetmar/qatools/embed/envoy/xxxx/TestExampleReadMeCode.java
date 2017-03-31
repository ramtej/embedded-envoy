/**
 * Copyright (C) 2011
 *   Michael Mosmann <michael@mosmann.de>
 *   Martin Jöhren <m.joehren@googlemail.com>
 *
 * with contributions from
 * 	konstantin-ba@github,
	Archimedes Trajano (trajano@github),
	Kevin D. Keck (kdkeck@github),
	Ben McCann (benmccann@github)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jetmar.qatools.embed.envoy.xxxx;

import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.config.store.FileSet;
import de.flapdoodle.embed.process.config.store.FileType;
import de.flapdoodle.embed.process.distribution.ArchiveType;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.distribution.GenericVersion;
import de.flapdoodle.embed.process.distribution.IVersion;
import org.junit.Test;

import java.io.IOException;

public class TestExampleReadMeCode {

	/*
	 * ### Usage
	 */

	// #### Build a generic process starter
	@Test
	public void genericProcessStarter() throws IOException {

		IVersion version=new GenericVersion("2.1.1");

		IRuntimeConfig config = new GenericRuntimeConfigBuilder()
			.name("phantomjs")
			//https://bitbucket.org/ariya/phantomjs/downloads/phantomjs-2.1.1-linux-x86_64.tar.bz2
			.downloadPath("https://bitbucket.org/ariya/phantomjs/downloads/")
			.packageResolver()
				.files(Distribution.detectFor(version), FileSet.builder().addEntry(FileType.Executable, "phantomjs").build())
				.archivePath(Distribution.detectFor(version), "phantomjs-"+version.asInDownloadPath()+"-linux-x86_64.tar.bz2")
				.archiveType(Distribution.detectFor(version), ArchiveType.TBZ2)
				.build()
			.build();


		GenericStarter starter = new GenericStarter(config);

		GenericExecuteable executable = starter.prepare(new GenericProcessConfig(version, null));

		GenericProcess process = executable.start();

		process.stop();

		executable.stop();
	}

	@Test
	public void genericProcessStarter2() throws IOException {

		IVersion version=new GenericVersion("1.2.0");

		IRuntimeConfig config = new GenericRuntimeConfigBuilder()
				.name("envoy")
		        // https://github.com/ramtej/distributions-envoy/raw/master/downloads/envoy-1.2.0-linux-x86_64.tar.gz
				.downloadPath("https://github.com/ramtej/distributions-envoy/raw/master/downloads/")
				.packageResolver()
					.files(Distribution.detectFor(version), FileSet.builder().addEntry(FileType.Executable, "envoy").build())
					.archivePath(Distribution.detectFor(version), "envoy-"+version.asInDownloadPath()+"-linux-x86_64.tar.gz")
					.archiveType(Distribution.detectFor(version), ArchiveType.TGZ)
				.build()
				.build();

		GenericStarter starter = new GenericStarter(config);

		GenericExecuteable executable = starter.prepare(new GenericProcessConfig(version, null));

		GenericProcess process = executable.start();

		// process.stop();

		// executable.stop();
	}
}
