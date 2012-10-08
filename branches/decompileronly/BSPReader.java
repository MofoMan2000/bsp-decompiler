// BSPReader class

// Does the actual reading of the BSP file and takes appropriate
// action based primarily on BSP version number. It also feeds all
// appropriate data to the different BSP version classes. This
// does not actually do any data processing or analysis, it simply
// reads from the hard drive and sends the data where it needs to go.
// Deprecates the LS class, and doesn't create a file for every lump!

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;

public class BSPReader {
	
	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	private File BSP; // Where's my BSP?
	private String folder;
	// Both MoHAA and Source engines use different version numbering systems than the standard.
	private boolean source=false;
	private boolean mohaa=false;
	private boolean raven=false;
	private boolean wad=false;
	private boolean ef2=false;
	
	public final int OFFSET=0;
	public final int LENGTH=1;
	// These are only used in Source BSPs, which have a lot of different structures
	public final int LUMPVERSION=2;
	public final int FOURCC=3;
	
	private int version=0;
	private int version2=0;
	
	// Declare all kinds of BSPs here, the one actually used will be determined by constructor
	// protected BSPv29n30
	protected DoomMap[] doomMaps;
	protected v38BSP BSP38;
	protected v42BSP BSP42;
	protected v46BSP BSP46;
	protected EF2BSP STEF2BSP;
	protected MoHAABSP MOHAABSP;
	protected RavenBSP ravenBSP;
	protected SourceBSP SourceBSPObject;
	
	// CONSTRUCTORS
	
	// Takes a String in and assumes it is a path. That path is the path to the file
	// that is the BSP and its name minus the .BSP extension is assumed to be the folder.
	// See comments below for clarification. Case does not matter on the extension, so it
	// could be .BSP, .bsp, etc.
	public BSPReader(String in) {
		new BSPReader(new File(in));
	}
	
	public BSPReader(File in) {
		BSP=in;
		if(!BSP.exists()) {
			Window.println("Unable to open source BSP file, please ensure the BSP exists.",Window.VERBOSITY_ALWAYS);
		} else {
			folder=BSP.getParent(); // The read string minus the .BSP is the lumps folder
			if(folder==null) {
				folder="";
			}
		}
	}
	
	// METHODS
	
	public void readBSP() {
		try {
			// Don't forget, Java uses BIG ENDIAN BYTE ORDER, so all numbers have to be read and written backwards.
			int version=getVersion();
			FileInputStream offsetReader;
			byte[] read=new byte[4];
			int offset;
			int length;
			if(mohaa) {
				Window.println("MOHAA BSP found (modified id Tech 3)",Window.VERBOSITY_ALWAYS);
				offsetReader = new FileInputStream(BSP);
				MOHAABSP = new MoHAABSP(BSP.getPath());
				offsetReader.skip(12); // Skip the file header, putting the reader into the offset/length pairs
				
				// Lump 00
				offsetReader.read(read); // Read 4 bytes
				offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
				offsetReader.read(read); // Read 4 more bytes
				length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
				MOHAABSP.setMTextures(readLump(offset, length));
				
				// Lump 01
				offsetReader.read(read); // Read 4 bytes
				offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
				offsetReader.read(read); // Read 4 more bytes
				length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
				MOHAABSP.setPlanes(readLump(offset, length));
				
				offsetReader.skip(64); // Do not need offset/length for lumps 2-9
				
				// Lump 10
				offsetReader.read(read); // Read 4 bytes
				offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
				offsetReader.read(read); // Read 4 more bytes
				length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
				MOHAABSP.setTexScales(readLump(offset, length));
				
				// Lump 11
				offsetReader.read(read); // Read 4 bytes
				offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
				offsetReader.read(read); // Read 4 more bytes
				length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
				MOHAABSP.setMBrushSides(readLump(offset, length));
				
				// Lump 12
				offsetReader.read(read); // Read 4 bytes
				offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
				offsetReader.read(read); // Read 4 more bytes
				length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
				MOHAABSP.setBrushes(readLump(offset, length));
				
				// Lump 13
				offsetReader.read(read); // Read 4 bytes
				offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
				offsetReader.read(read); // Read 4 more bytes
				length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
				MOHAABSP.setModels(readLump(offset, length));
				
				// Lump 14
				offsetReader.read(read); // Read 4 bytes
				offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
				offsetReader.read(read); // Read 4 more bytes
				length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
				MOHAABSP.setEntities(readLump(offset, length));
				
				offsetReader.skip(80); // Do not need offset/length for lumps 15-24
				
				// Lump 24
				offsetReader.read(read); // Read 4 bytes
				offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
				offsetReader.read(read); // Read 4 more bytes
				length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
				MOHAABSP.setStaticProps(readLump(offset, length));
				
				offsetReader.close();
				
				MOHAABSP.printBSPReport();
			} else {
				if(source) {
					int lumpVersion=0;
					switch(version) {
						// Gonna handle all Source BSP formats here.
						// Might have to deal with format differences later.
						// For now, focus on HL2, or v19.
						case 17: // Vampire: The Masquerades Bloodlines
						case 18: // HL2 Beta
						case 19: // HL2, CSS, DoDS
						case 20: // HL2E1, HL2E2, Portal, L4D, TF2
						case 21: // L4D2, Portal 2, CSGO
						case 22: // Dota 2
						case 23: // Also Dota 2? OMG HAX
							Window.println("Source BSP v"+version+" found",Window.VERBOSITY_ALWAYS);
							offsetReader = new FileInputStream(BSP);
							// Left 4 Dead 2, for some reason, made the order "version, offset, length" for lump header structure,
							// rather than the usual "offset, length, version". I guess someone at Valve got bored.
							boolean isL4D2=false;
							SourceBSPObject = new SourceBSP(BSP.getPath(),version);
							offsetReader.skip(8); // Skip the VBSP and version number
							
							/* Needed source BSP lumps
							I can easily add more to the parser later if this list needs to be expanded
							0 ents
							1 planes
							2 texdata
							3 vertices
							5 nodes
							6 texinfo
							7 faces
							10 leaves
							12 edges
							13 surfedges
							14 models
							17 leafbrushes
							18 brushes
							19 brushsides
							26 displacement info
							27 original faces
							33 Displacement vertices
							40 Pakfile - can just be dumped
							43 texdata strings
							44 texdata string table
							48 displacement triangles
							*/
							
							// Lump 00
							offsetReader.read(read); // Read 4 bytes
							offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.read(read); // Read 4 more bytes
							length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.read(read); // Read 4 more bytes
							lumpVersion=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.skip(4);
							if(offset<1036) { // This is less than the total length of the file header. Probably indicates a L4D2 map.
								isL4D2=true;   // Although unused lumps have everything set to 0 in their info, Entities are NEVER EVER UNUSED! EVER!
							}                 // A BSP file without entities is geometry with no life, no worldspawn. That's never acceptable.
							if(isL4D2) {
								SourceBSPObject.setEntities(readLump(length, version));
							} else {
								SourceBSPObject.setEntities(readLump(offset, length));
							}
							
							// Lump 01
							offsetReader.read(read); // Read 4 bytes
							offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.read(read); // Read 4 more bytes
							length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.read(read); // Read 4 more bytes
							lumpVersion=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.skip(4);
							if(isL4D2) {
								SourceBSPObject.setPlanes(readLump(length, version));
							} else {
								SourceBSPObject.setPlanes(readLump(offset, length));
							}
							
							// Lump 02
							offsetReader.read(read); // Read 4 bytes
							offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.read(read); // Read 4 more bytes
							length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.read(read); // Read 4 more bytes
							lumpVersion=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.skip(4);
							if(isL4D2) {
								SourceBSPObject.setTexDatas(readLump(length, version));
							} else {
								SourceBSPObject.setTexDatas(readLump(offset, length));
							}
							
							// Lump 03
							offsetReader.read(read); // Read 4 bytes
							offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.read(read); // Read 4 more bytes
							length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.read(read); // Read 4 more bytes
							lumpVersion=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.skip(4);
							if(isL4D2) {
								SourceBSPObject.setVertices(readLump(length, version));
							} else {
								SourceBSPObject.setVertices(readLump(offset, length));
							}
							
							offsetReader.skip(16); // Skip lump 4 data
							
							// Lump 05
							offsetReader.read(read); // Read 4 bytes
							offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.read(read); // Read 4 more bytes
							length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.read(read); // Read 4 more bytes
							lumpVersion=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.skip(4);
							if(isL4D2) {
								SourceBSPObject.setNodes(readLump(length, version));
							} else {
								SourceBSPObject.setNodes(readLump(offset, length));
							}
							
							// Lump 06
							offsetReader.read(read); // Read 4 bytes
							offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.read(read); // Read 4 more bytes
							length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.read(read); // Read 4 more bytes
							lumpVersion=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.skip(4);
							if(isL4D2) {
								SourceBSPObject.setTexInfos(readLump(length, version));
							} else {
								SourceBSPObject.setTexInfos(readLump(offset, length));
							}
							
							// Lump 07
							offsetReader.read(read); // Read 4 bytes
							offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.read(read); // Read 4 more bytes
							length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.read(read); // Read 4 more bytes
							lumpVersion=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.skip(4);
							if(isL4D2) {
							//	SourceBSPObject.setFaces(readLump(length, version));
							} else {
							//	SourceBSPObject.setFaces(readLump(offset, length));
							}
							
							offsetReader.skip(32); // skip lump 8 and 9 data
							
							// Lump 10
							offsetReader.read(read); // Read 4 bytes
							offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.read(read); // Read 4 more bytes
							length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.read(read); // Read 4 more bytes
							lumpVersion=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.skip(4);
							if(isL4D2) {
								SourceBSPObject.setLeaves(readLump(length, version));
							} else {
								SourceBSPObject.setLeaves(readLump(offset, length));
							}
							
							offsetReader.skip(16);
							
							// Lump 12
							offsetReader.read(read); // Read 4 bytes
							offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.read(read); // Read 4 more bytes
							length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.read(read); // Read 4 more bytes
							lumpVersion=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.skip(4);
							if(isL4D2) {
								SourceBSPObject.setEdges(readLump(length, version));
							} else {
								SourceBSPObject.setEdges(readLump(offset, length));
							}
							
							// Lump 13
							offsetReader.read(read); // Read 4 bytes
							offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.read(read); // Read 4 more bytes
							length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.read(read); // Read 4 more bytes
							lumpVersion=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.skip(4);
							if(isL4D2) {
								SourceBSPObject.setSurfEdges(readLump(length, version));
							} else {
								SourceBSPObject.setSurfEdges(readLump(offset, length));
							}
							
							// Lump 14
							offsetReader.read(read); // Read 4 bytes
							offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.read(read); // Read 4 more bytes
							length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.read(read); // Read 4 more bytes
							lumpVersion=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.skip(4);
							if(isL4D2) {
								SourceBSPObject.setModels(readLump(length, version));
							} else {
								SourceBSPObject.setModels(readLump(offset, length));
							}
							
							offsetReader.skip(32); // Skip lumps 15 and 16
							
							// Lump 17
							offsetReader.read(read); // Read 4 bytes
							offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.read(read); // Read 4 more bytes
							length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.read(read); // Read 4 more bytes
							lumpVersion=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.skip(4);
							if(isL4D2) {
								SourceBSPObject.setMarkBrushes(readLump(length, version));
							} else {
								SourceBSPObject.setMarkBrushes(readLump(offset, length));
							}
							
							// Lump 18
							offsetReader.read(read); // Read 4 bytes
							offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.read(read); // Read 4 more bytes
							length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.read(read); // Read 4 more bytes
							lumpVersion=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.skip(4);
							if(isL4D2) {
								SourceBSPObject.setBrushes(readLump(length, version));
							} else {
								SourceBSPObject.setBrushes(readLump(offset, length));
							}
							
							// Lump 19
							offsetReader.read(read); // Read 4 bytes
							offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.read(read); // Read 4 more bytes
							length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.read(read); // Read 4 more bytes
							lumpVersion=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.skip(4);
							if(isL4D2) {
								SourceBSPObject.setBrushSides(readLump(length, version));
							} else {
								SourceBSPObject.setBrushSides(readLump(offset, length));
							}
							
							offsetReader.skip(96); // Skip entries for lumps 20 21 22 23 24 25
							
							// Lump 26
							offsetReader.read(read); // Read 4 bytes
							offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.read(read); // Read 4 more bytes
							length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.read(read); // Read 4 more bytes
							lumpVersion=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.skip(4);
							if(isL4D2) {
								SourceBSPObject.setDispInfos(readLump(length, version));
							} else {
								SourceBSPObject.setDispInfos(readLump(offset, length));
							}
							
							// Lump 27
							offsetReader.read(read); // Read 4 bytes
							offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.read(read); // Read 4 more bytes
							length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.read(read); // Read 4 more bytes
							lumpVersion=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.skip(4);
							if(isL4D2) {
							//	SourceBSPObject.setOriginalFaces(readLump(length, version));
							} else {
							//	SourceBSPObject.setOriginalFaces(readLump(offset, length));
							}
							
							offsetReader.skip(80); // Lumps 28 29 30 31 32
							
							// Lump 33
							offsetReader.read(read); // Read 4 bytes
							offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.read(read); // Read 4 more bytes
							length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.read(read); // Read 4 more bytes
							lumpVersion=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.skip(4);
							if(isL4D2) {
								SourceBSPObject.setDispVerts(readLump(length, version));
							} else {
								SourceBSPObject.setDispVerts(readLump(offset, length));
							}
							
							offsetReader.skip(96);
							
							if(Window.extractZipIsSelected()) {
								// Lump 40
								offsetReader.read(read); // Read 4 bytes
								offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
								offsetReader.read(read); // Read 4 more bytes
								length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
								offsetReader.read(read); // Read 4 more bytes
								lumpVersion=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
								offsetReader.skip(4);
								try {
									Window.print("Extracting internal PAK file... ",Window.VERBOSITY_ALWAYS);
									Date begin=new Date();
									FileOutputStream PAKWriter;
									if(Window.getOutputFolder().equals("default")) {
										PAKWriter=new FileOutputStream(new File(SourceBSPObject.getPath().substring(0, SourceBSPObject.getPath().length()-4)+".pak"));
									} else {
										PAKWriter=new FileOutputStream(new File(Window.getOutputFolder()+"\\"+SourceBSPObject.getMapName().substring(0, SourceBSPObject.getMapName().length()-4)+".pak"));
									}
									if(isL4D2) {
										PAKWriter.write(readLump(length, version));
									} else {
										PAKWriter.write(readLump(offset, length));
									}
									PAKWriter.close();
									Date end=new Date();
									Window.println(end.getTime()-begin.getTime()+"ms",Window.VERBOSITY_ALWAYS);
								} catch(java.io.IOException e) {
									Window.println("WARNING: Unable to write PAKFile! Path: "+BSP.getAbsolutePath().substring(0,BSP.getAbsolutePath().length()-4)+".pak",Window.VERBOSITY_WARNINGS);
								}
							} else {
								offsetReader.skip(16);
							}
							
							offsetReader.skip(32);
							
							// Lump 43
							offsetReader.read(read); // Read 4 bytes
							offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.read(read); // Read 4 more bytes
							length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.read(read); // Read 4 more bytes
							lumpVersion=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.skip(4);
							if(isL4D2) {
								SourceBSPObject.setTextures(readLump(length, version));
							} else {
								SourceBSPObject.setTextures(readLump(offset, length));
							}
							
							// Lump 44
							offsetReader.read(read); // Read 4 bytes
							offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.read(read); // Read 4 more bytes
							length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.read(read); // Read 4 more bytes
							lumpVersion=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.skip(4);
							if(isL4D2) {
								SourceBSPObject.setTexTable(readLump(length, version));
							} else {
								SourceBSPObject.setTexTable(readLump(offset, length));
							}
							
							offsetReader.skip(48);
							
							// Lump 48
							offsetReader.read(read); // Read 4 bytes
							offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.read(read); // Read 4 more bytes
							length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.read(read); // Read 4 more bytes
							lumpVersion=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.skip(4);
							if(isL4D2) {
								SourceBSPObject.setDispTris(readLump(length, version));
							} else {
								SourceBSPObject.setDispTris(readLump(offset, length));
							}
							
							offsetReader.close();
							
							SourceBSPObject.printBSPReport();
							
						break;
					}
				} else {
					if(raven) {
						Window.println("Raven Software BSP found (Modified id Tech 3)",Window.VERBOSITY_ALWAYS);
						offsetReader = new FileInputStream(BSP);
						ravenBSP = new RavenBSP(BSP.getPath());
						offsetReader.skip(8); // Skip the file header, putting the reader into the offset/length pairs
						
						// Lump 00
						offsetReader.read(read); // Read 4 bytes
						offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
						offsetReader.read(read); // Read 4 more bytes
						length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
						ravenBSP.setEntities(readLump(offset, length));
						
						// Lump 01
						offsetReader.read(read); // Read 4 bytes
						offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
						offsetReader.read(read); // Read 4 more bytes
						length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
						ravenBSP.setTextures(readLump(offset, length));
						
						// Lump 02
						offsetReader.read(read); // Read 4 bytes
						offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
						offsetReader.read(read); // Read 4 more bytes
						length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
						ravenBSP.setPlanes(readLump(offset, length));
						
						offsetReader.skip(32); // Do not need offset/length for lumps 3-6
						
						// Lump 07
						offsetReader.read(read); // Read 4 bytes
						offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
						offsetReader.read(read); // Read 4 more bytes
						length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
						ravenBSP.setModels(readLump(offset, length));
						
						// Lump 08
						offsetReader.read(read); // Read 4 bytes
						offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
						offsetReader.read(read); // Read 4 more bytes
						length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
						ravenBSP.setBrushes(readLump(offset, length));
						
						// Lump 09
						offsetReader.read(read); // Read 4 bytes
						offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
						offsetReader.read(read); // Read 4 more bytes
						length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
						ravenBSP.setRBrushSides(readLump(offset, length));
						
						// Lump 10
						offsetReader.read(read); // Read 4 bytes
						offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
						offsetReader.read(read); // Read 4 more bytes
						length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
						ravenBSP.setRVertices(readLump(offset, length));
						
						offsetReader.skip(16); // Do not need offset/length for lumps 11 and 12
						
						// Lump 13
						offsetReader.read(read); // Read 4 bytes
						offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
						offsetReader.read(read); // Read 4 more bytes
						length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
						ravenBSP.setRFaces(readLump(offset, length));
						
						offsetReader.close();
						
						ravenBSP.printBSPReport();
					} else {
						if(ef2) {
							switch(version) {
								case 19:
									Window.println("Star Trek Elite Force 2 Demo BSP Found",Window.VERBOSITY_ALWAYS);
									STEF2BSP = new EF2BSP(BSP.getPath(), true);
									break;
								case 20:
									Window.println("Star Trek Elite Force 2 BSP Found",Window.VERBOSITY_ALWAYS);
									STEF2BSP = new EF2BSP(BSP.getPath(), false);
									break;
							}
							offsetReader = new FileInputStream(BSP);
							offsetReader.skip(12); // Skip the file header, putting the reader into the lump directory
							
							// Lump 00
							offsetReader.read(read); // Read 4 bytes
							offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.read(read); // Read 4 more bytes
							length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							STEF2BSP.setFTextures(readLump(offset, length));
							
							// Lump 01
							offsetReader.read(read); // Read 4 bytes
							offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.read(read); // Read 4 more bytes
							length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							STEF2BSP.setPlanes(readLump(offset, length));
							
							offsetReader.skip(80); // Lumps 02-11 aren't needed
							
							// Lump 12
							offsetReader.read(read); // Read 4 bytes
							offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.read(read); // Read 4 more bytes
							length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							STEF2BSP.setBrushSides(readLump(offset, length));
							
							// Lump 13
							offsetReader.read(read); // Read 4 bytes
							offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.read(read); // Read 4 more bytes
							length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							STEF2BSP.setBrushes(readLump(offset, length));
							
							offsetReader.skip(8);
							
							// Lump 15
							offsetReader.read(read); // Read 4 bytes
							offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.read(read); // Read 4 more bytes
							length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							STEF2BSP.setModels(readLump(offset, length));
							
							// Lump 16
							offsetReader.read(read); // Read 4 bytes
							offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							offsetReader.read(read); // Read 4 more bytes
							length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
							STEF2BSP.setEntities(readLump(offset, length));
							
							offsetReader.close();
							
							STEF2BSP.printBSPReport();
						} else {
							switch(version) {
								case 1: // WAD file
									Window.println("WAD file found",Window.VERBOSITY_ALWAYS);
									offsetReader = new FileInputStream(BSP);
									offsetReader.skip(4); // Skip the file header, putting the reader into the length and offset of the directory
									
									doomMaps=new DoomMap[0];
									
									// Find the directory
									offsetReader.read(read); // Read 4 bytes
									int numLumps=DataReader.readInt(read[0], read[1], read[2], read[3]);
									offsetReader.read(read); // Read 4 more bytes
									int directoryOffset=DataReader.readInt(read[0], read[1], read[2], read[3]);
									
									FileInputStream directoryReader = new FileInputStream(BSP);
									directoryReader.skip(directoryOffset);
									
									byte[] readDirectory=new byte[16];
									
									// Read through the directory to find maps
									for(int i=0;i<numLumps;i++) {
										directoryReader.read(readDirectory);
										offset=DataReader.readInt(readDirectory[0], readDirectory[1], readDirectory[2], readDirectory[3]);
										length=DataReader.readInt(readDirectory[4], readDirectory[5], readDirectory[6], readDirectory[7]);
										String lumpName=new String(new byte[] { readDirectory[8], readDirectory[9], readDirectory[10], readDirectory[11], readDirectory[12], readDirectory[13], readDirectory[14], readDirectory[15] });
										if( length==0 && ( lumpName.substring(0,3).equalsIgnoreCase("MAP") || ( lumpName.charAt(0)=='E' && lumpName.charAt(2)=='M' ) ) ) {
											String mapName=lumpName.substring(0,5); // Map names are always ExMy or MAPxx. Never more than five chars.
											Window.println("Map: "+mapName,Window.VERBOSITY_ALWAYS);
											// All of this code updates the maplist with a new entry
											DoomMap[] newList=new DoomMap[doomMaps.length+1];
											for(int j=0;j<doomMaps.length;j++) {
												newList[j] = doomMaps[j];
											}
											newList[doomMaps.length]=new DoomMap(BSP.getPath(), mapName);
											doomMaps=newList;
											
											FileInputStream lumpReader = new FileInputStream(BSP);
											lumpReader.skip(directoryOffset+((i+1)*16));
											
											lumpReader.read(readDirectory);
											
											offset=DataReader.readInt(readDirectory[0], readDirectory[1], readDirectory[2], readDirectory[3]);
											length=DataReader.readInt(readDirectory[4], readDirectory[5], readDirectory[6], readDirectory[7]);
											lumpName=new String(new byte[] { readDirectory[8], readDirectory[9], readDirectory[10], readDirectory[11], readDirectory[12], readDirectory[13], readDirectory[14], readDirectory[15] });
											if(lumpName.substring(0,6).equalsIgnoreCase("THINGS")) {
												doomMaps[doomMaps.length-1].setThings(readLump(offset, length));
											}
											
											lumpReader.read(readDirectory);
											
											offset=DataReader.readInt(readDirectory[0], readDirectory[1], readDirectory[2], readDirectory[3]);
											length=DataReader.readInt(readDirectory[4], readDirectory[5], readDirectory[6], readDirectory[7]);
											lumpName=new String(new byte[] { readDirectory[8], readDirectory[9], readDirectory[10], readDirectory[11], readDirectory[12], readDirectory[13], readDirectory[14], readDirectory[15] });
											if(lumpName.equalsIgnoreCase("LINEDEFS")) {
												doomMaps[doomMaps.length-1].setLinedefs(readLump(offset, length));
											}
											
											lumpReader.read(readDirectory);
											
											offset=DataReader.readInt(readDirectory[0], readDirectory[1], readDirectory[2], readDirectory[3]);
											length=DataReader.readInt(readDirectory[4], readDirectory[5], readDirectory[6], readDirectory[7]);
											lumpName=new String(new byte[] { readDirectory[8], readDirectory[9], readDirectory[10], readDirectory[11], readDirectory[12], readDirectory[13], readDirectory[14], readDirectory[15] });
											if(lumpName.equalsIgnoreCase("SIDEDEFS")) {
												doomMaps[doomMaps.length-1].setSidedefs(readLump(offset, length));
											}
											
											lumpReader.read(readDirectory);
											
											offset=DataReader.readInt(readDirectory[0], readDirectory[1], readDirectory[2], readDirectory[3]);
											length=DataReader.readInt(readDirectory[4], readDirectory[5], readDirectory[6], readDirectory[7]);
											lumpName=new String(new byte[] { readDirectory[8], readDirectory[9], readDirectory[10], readDirectory[11], readDirectory[12], readDirectory[13], readDirectory[14], readDirectory[15] });
											if(lumpName.equalsIgnoreCase("VERTEXES")) {
												doomMaps[doomMaps.length-1].setVertices(readLump(offset, length));
											}
											
											lumpReader.read(readDirectory);
											
											offset=DataReader.readInt(readDirectory[0], readDirectory[1], readDirectory[2], readDirectory[3]);
											length=DataReader.readInt(readDirectory[4], readDirectory[5], readDirectory[6], readDirectory[7]);
											lumpName=new String(new byte[] { readDirectory[8], readDirectory[9], readDirectory[10], readDirectory[11], readDirectory[12], readDirectory[13], readDirectory[14], readDirectory[15] });
											if(lumpName.substring(0,4).equalsIgnoreCase("SEGS")) {
												doomMaps[doomMaps.length-1].setSegments(readLump(offset, length));
											}
											
											lumpReader.read(readDirectory);
											
											offset=DataReader.readInt(readDirectory[0], readDirectory[1], readDirectory[2], readDirectory[3]);
											length=DataReader.readInt(readDirectory[4], readDirectory[5], readDirectory[6], readDirectory[7]);
											lumpName=new String(new byte[] { readDirectory[8], readDirectory[9], readDirectory[10], readDirectory[11], readDirectory[12], readDirectory[13], readDirectory[14], readDirectory[15] });
											if(lumpName.equalsIgnoreCase("SSECTORS")) {
												doomMaps[doomMaps.length-1].setSubSectors(readLump(offset, length));
											}
											
											lumpReader.read(readDirectory);
											
											offset=DataReader.readInt(readDirectory[0], readDirectory[1], readDirectory[2], readDirectory[3]);
											length=DataReader.readInt(readDirectory[4], readDirectory[5], readDirectory[6], readDirectory[7]);
											lumpName=new String(new byte[] { readDirectory[8], readDirectory[9], readDirectory[10], readDirectory[11], readDirectory[12], readDirectory[13], readDirectory[14], readDirectory[15] });
											if(lumpName.substring(0,5).equalsIgnoreCase("NODES")) {
												doomMaps[doomMaps.length-1].setNodes(readLump(offset, length));
											}
											
											lumpReader.read(readDirectory);
											
											offset=DataReader.readInt(readDirectory[0], readDirectory[1], readDirectory[2], readDirectory[3]);
											length=DataReader.readInt(readDirectory[4], readDirectory[5], readDirectory[6], readDirectory[7]);
											lumpName=new String(new byte[] { readDirectory[8], readDirectory[9], readDirectory[10], readDirectory[11], readDirectory[12], readDirectory[13], readDirectory[14], readDirectory[15] });
											if(lumpName.substring(0,7).equalsIgnoreCase("SECTORS")) {
												doomMaps[doomMaps.length-1].setSectors(readLump(offset, length));
											}
									
											lumpReader.close();
									
											doomMaps[doomMaps.length-1].printBSPReport();
											
											Window.window.addJob(null, doomMaps[doomMaps.length-1]);
										}
									}
									
									directoryReader.close();
									offsetReader.close();
								break;
								case 29: // Quake
								case 30: // Half-life
									Window.println("Sorry, no Quake/Half-life support (yet)!",Window.VERBOSITY_ALWAYS);
									break;
								case 38: // Quake 2
									Window.println("BSP v38 found (Quake 2)",Window.VERBOSITY_ALWAYS);
									offsetReader = new FileInputStream(BSP);
									BSP38 = new v38BSP(BSP.getPath());
									offsetReader.skip(8); // Skip the file header, putting the reader into the offset/length pairs
									
									// Lump 00
									offsetReader.read(read); // Read 4 bytes
									offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									offsetReader.read(read); // Read 4 more bytes
									length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									BSP38.setEntities(readLump(offset, length));
									
									// Lump 01
									offsetReader.read(read); // Read 4 bytes
									offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									offsetReader.read(read); // Read 4 more bytes
									length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									BSP38.setPlanes(readLump(offset, length));
									
									// Lump 02
									offsetReader.read(read); // Read 4 bytes
									offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									offsetReader.read(read); // Read 4 more bytes
									length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									BSP38.setVertices(readLump(offset, length));
									
									offsetReader.skip(8); // Do not need offset/length for lump 3
									
									// Lump 04
									offsetReader.read(read); // Read 4 bytes
									offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									offsetReader.read(read); // Read 4 more bytes
									length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									BSP38.setNodes(readLump(offset, length));
									
									// Lump 05
									offsetReader.read(read); // Read 4 bytes
									offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									offsetReader.read(read); // Read 4 more bytes
									length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									BSP38.setTextures(readLump(offset, length));
									
									// Lump 06
									offsetReader.read(read); // Read 4 bytes
									offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									offsetReader.read(read); // Read 4 more bytes
									length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									BSP38.setFaces(readLump(offset, length));
									
									offsetReader.skip(8); // Do not need offset/length for lumps 7
									
									// Lump 08
									offsetReader.read(read); // Read 4 bytes
									offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									offsetReader.read(read); // Read 4 more bytes
									length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									BSP38.setLeaves(readLump(offset, length));
									
									offsetReader.skip(8); // Do not need offset/length for lumps 9
									
									// Lump 10
									offsetReader.read(read); // Read 4 bytes
									offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									offsetReader.read(read); // Read 4 more bytes
									length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									BSP38.setMarkBrushes(readLump(offset, length));
									
									// Lump 11
									offsetReader.read(read); // Read 4 bytes
									offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									offsetReader.read(read); // Read 4 more bytes
									length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									BSP38.setEdges(readLump(offset, length));
									
									// Lump 12
									offsetReader.read(read); // Read 4 bytes
									offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									offsetReader.read(read); // Read 4 more bytes
									length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									BSP38.setMarkEdges(readLump(offset, length));
									
									// Lump 13
									offsetReader.read(read); // Read 4 bytes
									offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									offsetReader.read(read); // Read 4 more bytes
									length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									BSP38.setModels(readLump(offset, length));
									
									// Lump 14
									offsetReader.read(read); // Read 4 bytes
									offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									offsetReader.read(read); // Read 4 more bytes
									length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									BSP38.setBrushes(readLump(offset, length));
									
									// Lump 15
									offsetReader.read(read); // Read 4 bytes
									offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									offsetReader.read(read); // Read 4 more bytes
									length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									BSP38.setBrushSides(readLump(offset, length));
									/*
									offsetReader.skip(16); // Do not need offset/length for lumps 16 or 17
									
									// Lump 18
									offsetReader.read(read); // Read 4 bytes
									offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									offsetReader.read(read); // Read 4 more bytes
									length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									BSP38.setAreaPortals(readLump(offset, length));*/
									
									offsetReader.close();
									
									BSP38.printBSPReport();
									break;
								case 42: // JBN
									Window.println("BSP v42 found (Nightfire)",Window.VERBOSITY_ALWAYS);
									offsetReader = new FileInputStream(BSP);
									BSP42 = new v42BSP(BSP.getPath());
									offsetReader.skip(4); // Skip the file header, putting the reader into the offset/length pairs
									
									// Lump 00
									offsetReader.read(read); // Read 4 bytes
									offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									offsetReader.read(read); // Read 4 more bytes
									length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									BSP42.setEntities(readLump(offset, length));
									
									// Lump 01
									offsetReader.read(read); // Read 4 bytes
									offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									offsetReader.read(read); // Read 4 more bytes
									length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									BSP42.setPlanes(readLump(offset, length));
									
									// Lump 02
									offsetReader.read(read); // Read 4 bytes
									offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									offsetReader.read(read); // Read 4 more bytes
									length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									BSP42.setTextures(readLump(offset, length));
									
									// Lump 03
									offsetReader.read(read); // Read 4 bytes
									offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									offsetReader.read(read); // Read 4 more bytes
									length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									BSP42.setMaterials(readLump(offset, length));
									
									// Lump 04
									offsetReader.read(read); // Read 4 bytes
									offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									offsetReader.read(read); // Read 4 more bytes
									length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									BSP42.setVertices(readLump(offset, length));
									
									offsetReader.skip(32); // Do not need offset/length for lumps 5-8
									
									// Lump 09
									offsetReader.read(read); // Read 4 bytes
									offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									offsetReader.read(read); // Read 4 more bytes
									length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									BSP42.setFaces(readLump(offset, length));
									
									offsetReader.skip(8); // Do not need offset/length for lump 10
									
									// Lump 11
									offsetReader.read(read); // Read 4 bytes
									offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									offsetReader.read(read); // Read 4 more bytes
									length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									BSP42.setLeaves(readLump(offset, length));
									
									offsetReader.skip(8); // Do not need offset/length for lump 12
									
									// Lump 13
									offsetReader.read(read); // Read 4 bytes
									offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									offsetReader.read(read); // Read 4 more bytes
									length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									BSP42.setMarkBrushes(readLump(offset, length));
									
									// Lump 14
									offsetReader.read(read); // Read 4 bytes
									offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									offsetReader.read(read); // Read 4 more bytes
									length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									BSP42.setModels(readLump(offset, length));
									
									// Lump 15
									offsetReader.read(read); // Read 4 bytes
									offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									offsetReader.read(read); // Read 4 more bytes
									length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									BSP42.setBrushes(readLump(offset, length));
									
									// Lump 16
									offsetReader.read(read); // Read 4 bytes
									offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									offsetReader.read(read); // Read 4 more bytes
									length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									BSP42.setBrushSides(readLump(offset, length));
									
									// Lump 17
									offsetReader.read(read); // Read 4 bytes
									offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									offsetReader.read(read); // Read 4 more bytes
									length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									BSP42.setTextureMatrices(readLump(offset, length));
									
									offsetReader.close();
									
									BSP42.printBSPReport();
									break;
								case 46: // Quake 3/close derivative
								case 47: // RTCW
									Window.println("BSP v46 found (id Tech 3)",Window.VERBOSITY_ALWAYS);
									offsetReader = new FileInputStream(BSP);
									BSP46 = new v46BSP(BSP.getPath());
									offsetReader.skip(8); // Skip the file header, putting the reader into the offset/length pairs
									
									// Lump 00
									offsetReader.read(read); // Read 4 bytes
									offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									offsetReader.read(read); // Read 4 more bytes
									length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									BSP46.setEntities(readLump(offset, length));
									
									// Lump 01
									offsetReader.read(read); // Read 4 bytes
									offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									offsetReader.read(read); // Read 4 more bytes
									length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									BSP46.setTextures(readLump(offset, length));
									
									// Lump 02
									offsetReader.read(read); // Read 4 bytes
									offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									offsetReader.read(read); // Read 4 more bytes
									length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									BSP46.setPlanes(readLump(offset, length));
									
									offsetReader.skip(32); // Do not need offset/length for lumps 3-6
									
									// Lump 07
									offsetReader.read(read); // Read 4 bytes
									offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									offsetReader.read(read); // Read 4 more bytes
									length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									BSP46.setModels(readLump(offset, length));
									
									// Lump 08
									offsetReader.read(read); // Read 4 bytes
									offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									offsetReader.read(read); // Read 4 more bytes
									length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									BSP46.setBrushes(readLump(offset, length));
									
									// Lump 09
									offsetReader.read(read); // Read 4 bytes
									offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									offsetReader.read(read); // Read 4 more bytes
									length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									BSP46.setBrushSides(readLump(offset, length));
									
									// Lump 10
									offsetReader.read(read); // Read 4 bytes
									offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									offsetReader.read(read); // Read 4 more bytes
									length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									BSP46.setVertices(readLump(offset, length));
									
									offsetReader.skip(16); // Do not need offset/length for lumps 11 and 12
									
									// Lump 13
									offsetReader.read(read); // Read 4 bytes
									offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									offsetReader.read(read); // Read 4 more bytes
									length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									BSP46.setFaces(readLump(offset, length));
									
									offsetReader.close();
									
									BSP46.printBSPReport();
									
									break;
								default:
									Window.println("I don't know what kind of BSP this is! Please post an issue on the bug tracker!",Window.VERBOSITY_ALWAYS);
							}
						}
					}
				}
			}
		} catch(java.io.IOException e) {
			Window.println("Unable to access BSP file! Is it open in another program?",Window.VERBOSITY_ALWAYS);
		}
	}
	
	// +readLump(int, int)
	// Reads the lump length bytes long at offset in the file
	public byte[] readLump(int offset, int length) {
		byte[] input=new byte[length];
		try {
			FileInputStream fileReader=new FileInputStream(BSP);
			fileReader.skip(offset);
			fileReader.read(input);
			fileReader.close();
		} catch(java.io.IOException e) {
			Window.println("Unknown error reading BSP, it was working before!",Window.VERBOSITY_ALWAYS);
		}
		return input;
	}
				
	// ACCESSORS/MUTATORS
	
	public boolean isSource() {
		return source;
	}
	
	public boolean isRaven() {
		return raven;
	}
	
	public boolean isMOHAA() {
		return mohaa;
	}
	
	public boolean isEF2() {
		return ef2;
	}
	
	public int getVersion() throws java.io.IOException {
		if(version==0) {
			byte[] read=new byte[4];
			FileInputStream versionNumberReader=new FileInputStream(BSP); // This filestream will be used to read version number only
			versionNumberReader.read(read);
			int in=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
			if(in == 1347633737) { // 1347633737 reads in ASCII as "IBSP"
				versionNumberReader.read(read);
				version=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
			} else {
				if(in == 892416050) { // 892416050 reads in ASCII as "2015," the game studio which developed MoHAA
					mohaa=true;
					versionNumberReader.read(read);
					version=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff); // Should be 19
				} else {
				if(in == 1095516485) { // 1095516485 reads in ASCII as "EALA," the ones who developed MoHAA Spearhead and Breakthrough
						mohaa=true;
						versionNumberReader.read(read);
						version=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff); // Should be 21
					} else {
						if(in == 1347633750) { // 1347633750 reads in ASCII as "VBSP." Indicates Source engine.
							source=true;
							versionNumberReader.read(read);
							// Some source games handle this as 2 shorts. Since most version numbers
							// are below 65535, I can always read the first number as a short, because
							// the least significant bits come first in little endian.
							version=(int)DataReader.readShort(read[0], read[1]);
							version2=(int)DataReader.readShort(read[2], read[3]);
						} else {
							if(in==1347633746) { // Reads in ASCII as "RBSP". Raven software's modification of Q3BSP
								raven=true;
								versionNumberReader.read(read);
								version=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff); // Probably 1
							} else {
								if(in == 1263223110 || in == 556942917) { // "FAKK" or "EF2!"
									versionNumberReader.read(read);
									version=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
									if(version==19 || version==20) {
										ef2=true;
									}
								} else {
									if(in == 1145132873 || in == 1145132880) { // "IWAD" or "PWAD"
										wad=true;
										version=1;
									} else {
										version=in;
									}
								}
							}
						}
					}
				}
			}
		}
		return version;
	}
}
