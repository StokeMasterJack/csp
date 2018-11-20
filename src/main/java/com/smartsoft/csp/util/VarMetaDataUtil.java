package com.smartsoft.csp.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.smartsoft.csp.ast.Var;
import com.smartsoft.csp.ast.Xor;
import com.smartsoft.csp.util.varSets.VarSet;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;

import java.util.Collection;

public class VarMetaDataUtil {



    public static Element convertToXml(Multimap<String, Var> xorParentChildMultimap) {
        DocumentFactory f = new DocumentFactory();
        Element varMeta = f.createElement("var-meta");

        for (String groupCode : xorParentChildMultimap.keySet()) {

            Element group = f.createElement("group");
            group.addAttribute("code", groupCode);
            if (!groupCode.equals("ACY")) {
                group.addAttribute("radio", "true");
            }
            varMeta.add(group);

            Collection<Var> vars = xorParentChildMultimap.get(groupCode);
            for (Var var : vars) {
                Element varElement = f.createElement("vr");
                varElement.addAttribute("code", var.getVarCode());
                group.add(varElement);
            }
        }

        return varMeta;

    }


    public static Element reverseEngineerVarMetaForXor(VarSet varSet) {
        Multimap<String, Var> multimap = reverseEngineerVarMetaForXor1(varSet);
        return convertToXml(multimap);
    }

    public static Multimap<String, Var> reverseEngineerVarMetaForXor1(VarSet varSet) {
        Multimap<String, Var> xorGroups = HashMultimap.create();
        for (Var var : varSet) {
            Xor xorParent = var.getXorParent();
            if (xorParent == null) {
                //for now all checkboxes put into one group
                xorGroups.put("ACY", var);
            } else {
                assert var.isRadioVar();
                String groupCode = System.identityHashCode(xorParent) + "";
                xorGroups.put(groupCode, var);
            }
        }
        return xorGroups;

    }

}
