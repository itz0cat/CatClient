/*
 * Copyright © 2024 moehreag <moehreag@gmail.com> & Contributors
 * Modified by CatClient
 *
 * This file is part of CatClient.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * For more information, see the LICENSE file.
 */

package io.github.axolotlclient.credits;

import java.util.Collection;
import java.util.LinkedHashSet;

import lombok.Getter;

public class Credits {

	@Getter
	private static final Collection<Credits> contributors = new LinkedHashSet<>(), otherPeople = new LinkedHashSet<>();

	static {
		contributor("itz0cat", "CatClient Developer & Maintainer", "https://github.com/itz0cat/CatClient");
		otherPerson("AxolotlClient Team", "Forked from AxolotlClient", "Special thanks to moehreag and contributors for creating the original client!", "https://codeberg.org/moehreag/AxolotlClient");
	}

	@Getter
	private final String name;
	@Getter
	private final String[] things;

	private Credits(String name, String... things) {
		this.name = name;
		this.things = things;
	}

	private static void contributor(String name, String... things) {
		Credits c = new Credits(name, things);
		contributors.add(c);
	}

	private static void otherPerson(String name, String... things) {
		Credits c = new Credits(name, things);
		otherPeople.add(c);
	}
}
