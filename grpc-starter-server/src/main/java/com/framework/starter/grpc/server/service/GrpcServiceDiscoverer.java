/*
 * Copyright (c) 2016-2021 Michael Zhang <yidongnan@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.framework.starter.grpc.server.service;

import java.util.Collection;
import com.framework.starter.grpc.server.serverfactory.GrpcServerFactory;


/**
 * An interface for a bean that will be used to find grpc services and codecs. These will then be provided to the
 * {@link GrpcServerFactory} which then uses them to configure the server.
 *
 * @author Michael (yidongnan@gmail.com)
 * @since 5/17/16
 */
@FunctionalInterface
public interface GrpcServiceDiscoverer {

    /**
     * Find the grpc services that should provided by the server.
     *
     * @return The grpc services that should be provided. Never null.
     */
    Collection<GrpcServiceDefinition> findGrpcServices();

}
