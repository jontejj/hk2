/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 *
 * Contributor(s):
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */


package org.jvnet.hk2.osgiadapter;

import com.sun.enterprise.module.ManifestConstants;
import com.sun.enterprise.module.ModuleDefinition;
import com.sun.enterprise.module.ModuleDependency;
import com.sun.enterprise.module.ModuleMetadata;
import com.sun.enterprise.module.common_impl.Jar;
import org.osgi.framework.Constants;
import org.osgi.framework.Bundle;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * @author Sanjeeb.Sahoo@Sun.COM
 */
public class OSGiModuleDefinition implements ModuleDefinition {

    private String name;
    private URI location;
    private String version;
    private String lifecyclePolicyClassName;
    private Manifest manifest;
    private ModuleMetadata metadata = new ModuleMetadata();

    public OSGiModuleDefinition(File jar) throws IOException {
        this(Jar.create(jar), jar);
    }

    public OSGiModuleDefinition(Jar jarFile, File jar) throws IOException {
        /*
        * When we support reading metadata from external manifest.mf file,
        * we can create a custom URI and the uRL handler can merge the
        * manifest info. For now, just use the standard URI.
        */
        location = jar.toURI();
        manifest = jarFile.getManifest();
        Attributes mainAttr = manifest.getMainAttributes();
        name = mainAttr.getValue(Constants.BUNDLE_SYMBOLICNAME);
        version = mainAttr.getValue(Constants.BUNDLE_VERSION);
        if (version == null) version = "0.0.0"; // default in OSGi
        lifecyclePolicyClassName = mainAttr.getValue(ManifestConstants.LIFECYLE_POLICY);
        jarFile.loadMetadata(metadata);
    }

    public OSGiModuleDefinition(Bundle b) throws IOException, URISyntaxException {
        /*
         * TODO: We should create an adapter to Jar using Bundle.getEntryPaths
         * and use that instead of relying on getLocation.
         */
        this(new File(new URI(b.getLocation()).getPath()));
    }

    public String getName() {
        return name;
    }

    public String[] getPublicInterfaces() {
        throw new UnsupportedOperationException(
                "This method should not be called in OSGi environment, " +
                        "hence not supported");
    }

    public ModuleDependency[] getDependencies() {
        throw new UnsupportedOperationException(
                "This method should not be called in OSGi environment, " +
                        "hence not supported");
    }

    public URI[] getLocations() {
        return new URI[]{location};
    }

    public String getVersion() {
        return version;
    }

    public String getImportPolicyClassName() {
        throw new UnsupportedOperationException(
                "This method should not be called in OSGi environment, " +
                        "hence not supported");
    }

    public String getLifecyclePolicyClassName() {
        return lifecyclePolicyClassName;
    }

    public Manifest getManifest() {
        return manifest;
    }

    public ModuleMetadata getMetadata() {
        return metadata;
    }

    /**
     * Assists debugging.
     */
    @Override
    public String toString() {
        String bundleDescriptiveName =
                manifest.getMainAttributes().getValue(Constants.BUNDLE_NAME);
        return name + "(" + bundleDescriptiveName + ")" +':'+version;
    }

}
