/*
 * Copyright (C) 2020-2021  Nhalrath
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.crimsonite.rena.database;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.gen.ast.Db;
import com.rethinkdb.net.Connection;

import net.crimsonite.rena.RenaConfig;

public class DBConnection {

	private static final RethinkDB r = RethinkDB.r;
		
	/**
	 * @return database connection
	 */
	public static final Connection conn() {
		Connection connection = r.connection().hostname(RenaConfig.getHostName()).port(28015).connect();
		
		return connection;
	}
	
	// Primary DB
	protected static final Db db() {
		return r.db("Rena");
	}
}
