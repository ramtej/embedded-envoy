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

import de.flapdoodle.embed.process.config.store.FileSet;
import de.flapdoodle.embed.process.config.store.FileType;
import de.flapdoodle.embed.process.config.store.IPackageResolver;
import de.flapdoodle.embed.process.distribution.ArchiveType;
import de.flapdoodle.embed.process.distribution.Distribution;

import java.util.regex.Pattern;

/**
 * @see GenericRuntimeConfigBuilder.MapGenericPackageResolver
 * @author mosmann
 *
 */
@Deprecated
public class GenericPackageResolver implements IPackageResolver {

	public Pattern executeablePattern(Distribution distribution) {
		return Pattern.compile(".*"+executableFilename(distribution));
	}

	public String executableFilename(Distribution distribution) {
		switch (distribution.getPlatform()) {
			case Windows:
				return "phantomjs.exe";
		}
		return "phantomjs";
	}

	@Override
	public FileSet getFileSet(Distribution distribution) {
		String execName="phantomjs";
		switch (distribution.getPlatform()) {
			case Windows:
				execName="phantomjs.exe";
				break;
		}
		
		return FileSet.builder().addEntry(FileType.Executable,execName).build();
	}
	

	@Override
	public ArchiveType getArchiveType(Distribution distribution) {
		switch (distribution.getPlatform()) {
			case OS_X:
			case Windows:
				return ArchiveType.ZIP;
		}
		return ArchiveType.TBZ2;
	}

	@Override
	public String getPath(Distribution distribution) {
		final String packagePrefix;
		String bitVersion="";
		switch (distribution.getPlatform()) {
			case OS_X:
				packagePrefix="macosx";
				break;
			case Windows:
				packagePrefix="windows";
				break;
			default:
				packagePrefix="linux";
				switch (distribution.getBitsize()) {
					case B64:
						bitVersion="-x86_64";
						break;
					default:
						bitVersion="-i686";
				}
		}
		
		String packageExtension=".zip";
		if (getArchiveType(distribution)== ArchiveType.TBZ2) {
			packageExtension=".tar.bz2";
		}
		return "phantomjs-"+distribution.getVersion().asInDownloadPath()+"-"+packagePrefix+bitVersion+packageExtension;
	}
}