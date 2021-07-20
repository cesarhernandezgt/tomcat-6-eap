/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.coyote.http11;

import org.apache.coyote.InputBuffer;
import org.apache.tomcat.util.res.StringManager;

public abstract class AbstractInputBuffer implements InputBuffer {
	protected static final StringManager sm = StringManager.getManager("org.apache.coyote.http11");
    
    /**
     * Do HTTP headers with illegal names and/or values cause the request to be
     * rejected? Note that the field name is not quite right but cannot be
     * easily changed without breaking binary compatibility.
     */
    protected boolean rejectIllegalHeader;
    
    protected byte prevChr = 0;
    protected byte chr = 0;
}