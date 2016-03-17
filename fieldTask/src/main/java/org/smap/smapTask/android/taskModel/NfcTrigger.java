/*
 * Copyright (C) 2011 Smap Consulting Pty Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

/*
 * Class to store NFC trigger
 */
package org.smap.smapTask.android.taskModel;

import org.smap.smapTask.android.loaders.TaskEntry;

public class NfcTrigger {
	public String uid;
	public long tId;
    public int position;

    public NfcTrigger(long tId, String uid, int position) {
        this.uid = uid;
        this.tId = tId;
        this.position = position;
    }
}
