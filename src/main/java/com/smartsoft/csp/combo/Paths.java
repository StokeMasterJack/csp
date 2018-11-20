package com.smartsoft.csp.combo;


import com.smartsoft.csp.util.SpaceJvm;
import com.smartsoft.csp.VarInfo;
import com.smartsoft.csp.ast.Csp;
import com.smartsoft.csp.ast.Exp;
import com.smartsoft.csp.ast.Space;
import com.smartsoft.csp.ast.VarInf;
import com.smartsoft.csp.ssutil.Path;

public class Paths extends SpaceJvm {

    public static final Paths EFC_ORIGINAL = new Paths("efcOriginal");
    public static final Paths YARIS_1 = new Paths("yaris1");
    public static final Paths YARIS_2 = new Paths("yaris2");

    public static final String EXT_RAW = ".txt";
    public static final String EXT_DNNF = ".dnnf.txt";

    public static final String FACTORY = "fact";
    public static final String INVENTORY = "inventory";
    public static final String COMBO = "combo";

    public static final String FACTORY_RAW = FACTORY + EXT_RAW;
    public static final String INVENTORY_RAW = INVENTORY + EXT_RAW;

    public static final String FACTORY_DNNF = FACTORY + EXT_DNNF;
    public static final String INVENTORY_DNNF = INVENTORY + EXT_DNNF;
    public static final String COMBO_DNNF = COMBO + EXT_DNNF;

    public static Path cspDir = new Path("csp");
    public static Path invDir = new Path(cspDir, "inventory");

    public static final Path VAR_INFO = new Path(invDir, "varInfo.txt");


    public String groupName;
    public Path pGroup;

    public Path pVarInfo = VAR_INFO;
    public Path pFactory;
    public Path pInventory;

    public boolean fileSystem;

    public String clobVarInfo;
    public String clobFactory;
    public String clobInventory;

    public String clobDnnfFactory;

    public Paths() {
    }

    public Paths(String groupName) {
        this.groupName = groupName;
    }

    public Path getPVarInfo() {
        if (pVarInfo == null) {
            return VAR_INFO;
        }
        return pVarInfo;
    }

    public Path getPFactoryRaw() {
        if (pFactory == null) {
            return new Path(getPGroup(), FACTORY_RAW);
        }
        return pFactory;
    }

    public Path getPInventoryRaw() {
        if (pInventory == null) {
            return new Path(getPGroup(), INVENTORY_RAW);
        }
        return pInventory;
    }

    public Path getPFactoryDnnf() {
        return getPGroup().append(FACTORY_DNNF);
    }

    public Path getPInventoryDnnf() {
        return getPGroup().append(INVENTORY_DNNF);
    }

    public Path getPComboDnnf() {
        return getPGroup().append(COMBO_DNNF);
    }

    private Path getPGroup() {
        if (pGroup == null) {
            return new Path(cspDir, groupName);
        }
        return pGroup;
    }


    public String getClobVarInfo() {
        if (clobVarInfo == null) {
            clobVarInfo = loadClob(getPVarInfo());
        }
        return clobVarInfo;
    }

    public String getClobFactory() {
        if (clobFactory == null) {
            Path pFactoryRaw = getPFactoryRaw();
            clobFactory = loadClob(pFactoryRaw);
        }
        return clobFactory;
    }

    public String getClobInventory() {
        if (clobInventory == null) {
            clobInventory = loadClob(getPInventoryRaw());
        }
        return clobInventory;
    }

    public String getClobDnnfFactory() {
        if (clobDnnfFactory == null) {
            clobDnnfFactory = loadClob(getPFactoryDnnf());
        }
        return clobDnnfFactory;
    }

    public void loadClobs() {
        if (fileSystem) {
            loadClobsFromFileSystem();
        } else {
            loadClobsFromClasspath();
        }

    }

    public String loadClob(Path path) {
        if (fileSystem) {
            return loadFile(path);
        } else {
            return loadResource(path);
        }
    }

    public void loadClobsFromClasspath() {
        clobVarInfo = loadResource(getPVarInfo());
        clobFactory = loadResource(getPFactoryRaw());
        clobInventory = loadResource(getPInventoryRaw());
        clobDnnfFactory = loadResource(getPFactoryDnnf());
    }

    public void loadClobsFromFileSystem() {
        clobVarInfo = loadFile(getPVarInfo());
        clobFactory = loadFile(getPFactoryRaw());
        clobInventory = loadFile(getPInventoryRaw());
        clobDnnfFactory = loadFile(getPFactoryDnnf());
    }

    public VarInfo createVarInfo() {
        String clobVarInfo = getClobVarInfo();
        return new VarInf(clobVarInfo);
    }

    public Space createSpaceFactory() {
        String clob = getClobFactory();
        return Csp.parse(clob).getSpace();
    }






    public Exp createDNodeFactory() {

        VarInfo varInfo = createVarInfo();
        Path pFactoryDnnf = getPFactoryDnnf();
        String dnnfClob = loadClob(pFactoryDnnf);

        Exp dNode;

        if (dnnfClob == null) {
            Path p = cspDir.append(groupName).append(FACTORY_DNNF);
            System.err.println("Generating [" + groupName + "]...");

            String clobFactory = getClobFactory();
            Csp csp = Csp.parse(clobFactory);

            dNode = csp.toDnnf().gc();
            dNode.checkDnnf();

            assert csp.getSpace() != dNode.getSpace();

            System.err.println("p: " + p);

            System.err.println("dNode:");
            System.err.println(dNode.toString());


            Space space2 = dNode.getSpace();

            dnnfClob = space2.serializeTinyDnnf();
            writeText(p, dnnfClob);
            System.err.println("  Complete");
            System.err.println("  Created file: " + p);


        } else {
            dNode = Exp.Companion.parseTinyDnnf(dnnfClob);

            Space space = dNode.getSpace();
            space.setVarInfo(createVarInfo());
        }


        return dNode;

    }





    public static String loadResource(Path path) {
        try {
            return SpaceJvm.loadResource(path);
        } catch (Exception e) {
            System.err.println("Resource not found[" + path + "]");
            return null;
        }
    }

    public static String loadFile(Path path) {
        try {
            return SpaceJvm.loadFile(path);
        } catch (Exception e) {
            System.err.println("File not found[" + path + "]");
            return null;
        }
    }
}

