import org.apache.commons.lang3.StringUtils

import java.util.regex.Matcher
import java.util.regex.Pattern

Pattern patternVehicle = Pattern.compile(
        ".*?([0-9/]+)\\s+([0-9/]+).*?([0-9/]+)\\s+([0-9/]+)\\s+([0-9/]+)\\s+([0-9/]+)\\s+([0-9/]+)\\s+([0-9/]+).*",
        Pattern.MULTILINE)

Pattern patterDrone = Pattern.compile(
        "^.+?([0-9\\/]+)\\s+([0-9\\/A-Zna]+).*?([0-9\\/]+)\\s+([0-9\\(\\)\\/]+)\\s+([0-9\\/]+)\\s+([0-9\\/]+)\\s+([0-9\\/]+)\\s+.*\$"
        , Pattern.MULTILINE
)


List<String> SecurityList = Arrays.asList(
        "Groundcraft Security Blitzkrieg 4/3 4 2 10 8 3 4 2 14R 46,000¥ SR5:R5.0",
        "Groundcraft 	Security 	Charger 	4/3 	5 	2 	12 	12 	4 	4 	5 	16R 	65,000¥ 	SR5:R5.0",
        "Groundcraft 	Security 	Command (General) 	3/3 	4 	1 	20 	16 	5 	7 	10 	18R 	344,000¥ 	SR5:R5.0",
        "Groundcraft 	Security 	Goliath 	3/2 	4 	2 	16 	16 	3 	3 	8 	20R 	120,000¥ 	SR5:R5.0",
        "Groundcraft 	Security 	i8 Interceptor 	5/3 	8 	4 	12 	8 	4 	4 	3 	16R 	114,000¥ 	SR5:R5.0",
        "Groundcraft 	Security 	Luxus 	5/5 	5 	3 	18 	16 	5 	6 	8 	14R 	398,000¥ 	SR5:R5.0",
        "Groundcraft 	Security 	Rhino 	4/4 	4 	2 	17 	18 	4 	4 	9 	18R 	225,000¥ 	SR5:R5.0",
        "Groundcraft 	Security 	Stallion 	3/4 	5 	3 	16 	12 	3 	3 	4 	16R 	78,000¥ 	SR5:R5.0",
        "Groundcraft 	Security 	Stürmwagon 	5/4 	4 	2 	17 	18 	4 	5 	10 	20R 	145,000¥ 	SR5:R5.0",
        "Groundcraft 	Security 	Teufelkatze 	5/4 	5 	3 	16 	16 	3 	3 	7 	16F 	76,000¥ 	SR5:R5.0",
        "Groundcraft 	Security 	Trailer (General) 	3/3 	3 	1 	20 	16 	3 	7 	1 	18R 	54,000¥ 	SR5:R5.0",
        "Groundcraft 	Security 	Wolf II 	3/3 	3 	2 	24 	12 	2 	2 	6 	20F 	330,000¥ 	SR5:R5.0"
)

List<String> BoatList = Arrays.asList(
        "Watercraft 	Boat 	Samuvani Criscraft Otter 	4 	3 	2 	12 	6 	2 	2 	8 	- 	21,000¥ 	Core",
        "Watercraft 	Boat 	Yongkang Gala Trinity 	5 	6 	3 	10 	6 	1 	1 	3 	- 	37,000¥ 	Core",
        "Watercraft 	Boat 	Morgan Cutlass 	5 	4 	2 	16 	10 	3 	5 	6 	- 	96,000¥ 	Core",
        "Watercraft 	Boat 	AirRanger 	4 	4 	3 	10 	6 	1 	1 	6 	6 	25,500¥ 	SR5:R5.0",
        "Watercraft 	Boat 	AirRanger Heavy 	4 	4 	3 	12 	6 	1 	1 	5 	8 	35,500¥ 	SR5:R5.0",
        "Watercraft 	Boat 	Aquavida 	2 	1 	2 	20 	16 	1 	3 	2/8 	10 	115,000¥ 	SR5:R5.0",
        "Watercraft 	Boat 	Aquavida 	2 	1 	2 	20 	16 	1 	3 	4/8 	12 	135,000¥ 	SR5:R5.0",
        "Watercraft 	Boat 	Classic 	3 	4 	2 	24 	14 	4 	4 	14 	16 	14,870¥ 	SR5:R5.0",
        "Watercraft 	Boat 	Cottonmouth 	5 	7 	4 	8 	4 	3 	3 	4 	12 	120,000¥ 	SR5:R5.0",
        "Watercraft 	Boat 	Kingfisher 	3 	3 	2 	16 	12 	3 	4 	6 	12 	61,000¥ 	SR5:R5.0",
        "Watercraft 	Boat 	Lake King 	2 	3 	2 	14 	8 	1 	1 	8 	- 	35,000¥ 	SR5:R5.0",
        "Watercraft 	Boat 	Manta Ray 	4 	5 	3 	9 	6 	1 	1 	3 	- 	16,000¥ 	SR5:R5.0",
        "Watercraft 	Boat 	Mobius 	3 	3 	2 	36 	14 	6 	5 	22 	36 	84,980¥ 	SR5:R5.0",
        "Watercraft 	Boat 	Nightrunner 	5 	6 	3 	12 	6 	3 	4 	6 	10 	56,000¥ 	SR5:R5.0",
        "Watercraft 	Boat 	Riverine Military 	5 	5 	2 	20 	20 	6 	6 	8 	20F 	225,000¥ 	SR5:R5.0",
        "Watercraft 	Boat 	Riverine Police 	4 	5 	3 	16 	14 	4 	5 	8 	15R 	154,000¥ 	SR5:R5.0",
        "Watercraft 	Boat 	Riverine Security 	4 	5 	3 	16 	12 	4 	4 	8 	15R 	100,000¥ 	SR5:R5.0",
        "Watercraft 	Boat 	Stingray 	5 	5 	3 	8 	6 	1 	1 	2 	- 	13,000¥ 	SR5:R5.0",
        "Watercraft 	Boat 	Water Strider 	3 	2 	1 	8 	5 	2 	2 	1 	16 	11,000¥ 	SR5:R5.0",
        "Watercraft 	Boat 	Waterbug 	6 	3 	2 	8 	4 	1 	0 	2 	- 	8,000¥ 	SR5:R5.0",
        "Watercraft 	Boat 	Waterking 	3 	3 	2 	14 	8 	3 	2 	12 	12 	74,000¥ 	SR5:R5.0",
        "Watercraft 	Boat 	Waveskipper 	5 	3 	2 	10 	4 	1 	0 	1 	- 	10,000¥ 	SR5:R5.0",
        "Watercraft 	Boat 	Zodiac Scorpio 	4 	4 	2 	10 	6 	1 	1 	2/6 	8 	26,000¥ 	SR5:R5.0"
)

List<String> sailBoatList = Arrays.asList(
        "Watercraft 	Sailboat 	Elysium 	1/3 	1/4 	1/2 	14 	10 	2 	3 	6 	12 	78,000¥ 	SR5:R5.0",
        "Watercraft 	SailBoat 	Panther 	1/3 	2/5 	1/3 	18 	10 	2 	3 	8 	12 	135,000¥ 	SR5:R5.0",
        "Watercraft 	SailBoat 	Trident 	1/3 	4/5 	2/3 	16 	10 	2 	3 	6 	12 	125,000¥ 	SR5:R5.0",
        "Watercraft 	SailBoat 	Triton 	1 	6 	2 	16 	10 	2 	3 	6 	- 	104,000¥ 	SR5:R5.0"
)

List<String> SubmarineList = Arrays.asList(
        "Watercraft 	Submarine 	Proteus Lamprey 	3 	2 	1 	6 	6 	1 	3 	4 	- 	14,000¥ 	Core",
        "Watercraft 	Submarine 	Vulkan Electronaut 	3 	3 	1 	12 	10 	4 	4 	2 	- 	108,000¥ 	Core"
)

List<String> FixedWingList = Arrays.asList(
        "Aircraft 	Fixed-Wing 	Artemis Industries Nightwing 	6 	3 	1 	4 	0 	1 	1 	1 	- 	20,000¥ 	Core",
        "Aircraft 	Fixed-Wing 	Cessna C750 	3 	5 	3 	18 	4 	2 	2 	4 	- 	146,000¥ 	Core",
        "Aircraft 	Fixed-Wing 	Renault-Fiat Fokker Tundra-9 	3 	4 	3 	20 	10 	3 	3 	24 	- 	300,000¥ 	Core",
        "Aircraft 	Fixed-Wing 	PBY-70 Catalina II 	4 	3 	3 	22 	14 	3 	4 	16 	12 	250,000¥ 	SR5:R5.0"
)

List<String> RotorCraftList = Arrays.asList(
        "Aircraft 	Rotorcraft 	Ares Dragon 	4 	4 	3 	22 	8 	3 	3 	18 	- 	355,000¥ 	Core",
        "Aircraft 	Rotorcraft 	Nissan Hound 	5 	4 	3 	16 	16 	2 	4 	12 	- 	425,000¥ 	Core",
        "Aircraft 	Rotorcraft 	Northrup Wasp 	5 	5 	3 	10 	8 	3 	3 	1 	- 	86,000¥ 	Core",
        "Aircraft 	Rotorcraft 	Agular GX-2 	5 	7 	5 	20 	16 	4 	5 	2 	28F 	500,000¥ 	SR5:R5.0",
        "Aircraft 	Rotorcraft 	Agular GX-3AT 	4 	6 	4 	22 	20 	4 	4 	10 	28F 	550,000¥ 	SR5:R5.0",
        "Aircraft 	Rotorcraft 	“Lift-Ticket” ALS-669 	5 	3 	3 	16 	12 	3 	4 	5 	14 	325,000¥ 	SR5:R5.0",
        "Aircraft 	Rotorcraft 	Sea Sprite 	5 	4 	3 	18 	12 	3 	5 	14 	18R 	400,000¥ 	SR5:R5.0",
        "Aircraft 	Rotorcraft 	SKA-008 	6 	5 	8 	16 	18 	4 	4 	12 	24R 	525,000¥ 	SR5:R5.0",
        "Aircraft 	Rotorcraft 	Stallion 	5 	5 	4 	16 	16 	4 	4 	8 	12 	440,000¥ 	SR5:R5.0"
)

List<String> VTOLList = Arrays.asList(
        "Aircraft 	VTOL/VSTOL 	Ares Venture 	5 	7 	4 	16 	14 	4 	4 	6 	- 	400,000¥ 	Core",
        "Aircraft 	VTOL/VSTOL 	GMC Banshee 	6 	8 	4 	20 	18 	4 	6 	12 	- 	2,500,000¥ 	Core",
        "Aircraft 	VTOL/VSTOL 	Federated Boeing Commuter 	3 	3 	3 	16 	8 	3 	3 	30 	- 	350,000¥ 	Core",
        "Aircraft 	VTOL/VSTOL 	Gryphon 	5 	8 	7 	24 	24 	4 	5 	2 	28F 	3,200,000¥ 	SR5:R5.0",
        "Aircraft 	VTOL/VSTOL 	“Krime Wing” 4 	6 	5 	22 	18 	4 	5 	10 	(20) 	24F 	2,275,000¥ 	SR5:R5.0"
)

List<String> AirshipList = Arrays.asList(
        "Aircraft 	Airship 	LZP-2070 	4 	2 	3 	12 	6 	5 	4 	6 	12 	85,000¥ 	SR5:R5.0",
        "Aircraft 	Airship 	“Mothership” LAVH 	3 	3 	3 	10 	5 	3 	4 	1 	24R 	50,000¥ 	SR5:R5.0"
)

List<String> DroneMicroList = Arrays.asList(
        "Micro 	Shiawase Kanmushi 	4 	2 	1 	0 	0 	3 	3 	- 	8 	1,000¥ 	Core",
        "Micro 	Silkorsky-Bell Microskimmer 	3 	3 	1 	0 	0 	3 	3 	- 	6 	1,000¥ 	Core",
        "Micro 	Goldfish 	2/4 	1W 	1 	0 	0 	2 	2 	- 	6 	500¥ 	SR5:R5.0",
        "Micro 	NoizQuito 	4 	3R 	2 	1 	0 	3 	3 	- 	10R 	2,000¥ 	SR5:R5.0"
)

List<String> DroneMiniList = Arrays.asList(
        "Mini 	MCT Fly-Spy 	4 	3 	2 	1 	0 	3 	3 	- 	8 	2,000¥ 	Core",
        "Mini 	Horizon Flying Eye 	4 	3 	2 	1 	0 	3 	3 	- 	8 	2,000¥ 	Core",
        "Mini 	Condor 	2 	0R 	0 	1(1) 	0 	2 	4 	- 	6R 	4,000¥ 	SR5:R5.0",
        "Mini 	Dragonfly 	4 	2P 	1 	1(0) 	3 	3 	2 	- 	12R 	4,000¥ 	SR5:R5.0",
        "Mini 	Gerbil 	4/2 	2G 	1 	1(1) 	0 	2 	2 	- 	4 	2,000¥ 	SR5:R5.0",
        "Mini 	Hedgehog 	3 	1G 	1 	1(0) 	0 	4 	3 	- 	8F 	8,000¥ 	SR5:R5.0",
        "Mini 	Horizon CU^3 	4 	1P 	1 	1(1) 	0 	2 	3 	- 	4 	3,000¥ 	SR5:R5.0",
        "Mini 	Pigeon 2.0 	4 	2P 	1 	1(1) 	0 	2 	2 	- 	8 	3,000¥ 	SR5:R5.0"
)

List<String> DroneSmallList = Arrays.asList(
        "Small 	Aztechnology Crawler 	4 	3 	1 	3 	3 	4 	3 	- 	4 	4,000¥ 	Core",
        "Small 	Lockheed Optic-X2 	4 	4 	3 	2 	2 	3 	3 	- 	10 	21,000¥ 	Core",
        "Small 	Bloodhound 	3 	1G 	1 	2(0) 	0 	2 	4 	- 	8 	10,000¥ 	SR5:R5.0",
        "Small 	Castle Guard 	4/2 	1G 	1 	2(0) 	6 	3 	2 	- 	8R 	10,000¥ 	SR5:R5.0",
        "Small 	Ferret RPD-5X 	4/2 	1G 	1 	2(1) 	3 	3 	3 	- 	8R 	4,000¥ 	SR5:R5.0",
        "Small 	Gun Turret 	0 	0 	0 	2(0) 	6 	3 	2 	- 	4R 	4,000¥ 	SR5:R5.0",
        "Small 	Jardinero 	2/4 	1G 	1 	2(1) 	0 	2 	2 	- 	4 	2,000¥ 	SR5:R5.0",
        "Small 	Job-a-Mat 	0 	0 	0 	2(2) 	0 	2 	2 	- 	4 	3,000¥ 	SR5:R5.0",
        "Small 	Knight Errant P5 	4/2 	6G 	2 	2(1) 	0 	3 	2 	- 	10R 	8,000¥ 	SR5:R5.0",
        "Small 	Krake 	5 	3W 	4 	2(0) 	2 	4 	3 	- 	18F 	10,000¥ 	SR5:R5.0",
        "Small 	Mini-Zep 	2 	0P 	0 	2(4) 	0 	2 	2 	- 	4 	2,000¥ 	SR5:R5.0",
        "Small 	Pelican 	4 	2P 	1 	2(1) 	0 	2 	2 	- 	2 	4,000¥ 	SR5:R5.0",
        "Small 	Prairie Dog 	2/4 	2G 	1 	2(0) 	3 	3 	4 	- 	12F 	8,000¥ 	SR5:R5.0",
        "Small 	Proletarian 	4/2 	2G 	1 	2(1) 	0 	2 	2 	- 	6 	4,000¥ 	SR5:R5.0",
        "Small 	Renraku Dove 	4 	2P 	1 	2(1) 	0 	2 	2 	- 	4 	5,000¥ 	SR5:R5.0",
        "Small 	Sentry V 	4/0 	1G 	1 	2(0) 	6 	3 	2 	- 	4R 	4,000¥ 	SR5:R5.0",
        "Small 	Seven (Wheelie) 	4/2 	2G 	1 	1(3) 	0 	1 	1 	- 	- 	2,000¥ 	SR5:R5.0",
        "Small 	Seven (Treads) 	3 	2G 	1 	1(3) 	0 	1 	1 	- 	2 	2,000¥ 	SR5:R5.0",
        "Small 	Seven (Dirty) 	2/4 	2G 	1 	1(3) 	0 	1 	1 	- 	2 	2,000¥ 	SR5:R5.0",
        "Small 	Seven (Quad) 	4 	1G 	1 	1(3) 	0 	1 	1 	- 	4 	2,000¥ 	SR5:R5.0",
        "Small 	Seven (Swims) 	3 	2W 	1 	1(3) 	0 	1 	1 	- 	4 	1,000¥ 	SR5:R5.0",
        "Small 	Seven (Hovers) 	4 	1P 	1 	1(3) 	0 	1 	1 	- 	6 	4,000¥ 	SR5:R5.0",
        "Small 	Seven (Soars) 	3 	2J 	1 	1(3) 	0 	1 	1 	- 	8 	4,000¥ 	SR5:R5.0",
        "Small 	Sewer Snake 	3 	1G/1W 	1/1 	2(1) 	0 	2 	2 	- 	10 	6,000¥ 	SR5:R5.0",
        "Small 	Shamus 	3 	3G 	1 	4(0) 	4 	3 	8 	- 	10 	30,000¥ 	SR5:R5.0",
        "Small 	Smoke Generator 	3 	1G 	1 	2(0) 	0 	2 	2 	- 	8 	4,000¥ 	SR5:R5.0",
        "Small 	Sundowner 	3 	4P 	1 	2(0) 	0 	2 	2 	- 	8 	10,000¥ 	SR5:R5.0",
        "Small 	Wolfhound 	3 	2J 	1 	2(1) 	0 	2 	4 	- 	12 	30,000¥ 	SR5:R5.0"
)

List<String> DroneMediumList = Arrays.asList(
        "Medium 	Ares Duelist 	3 	3 	1 	4 	4 	3 	3 	- 	5R 	4,500¥ 	Core",
        "Medium 	GM-Nissan Doberman 	5 	3 	1 	4 	4 	3 	3 	- 	4R 	5,000¥ 	Core",
        "Medium 	MCT-Nissan Roto-Drone 	4 	4 	2 	4 	4 	3 	3 	- 	6 	5,000¥ 	Core",
        "Medium 	Cheetah 	4 	6G 	2 	2(0) 	6 	3 	2 	- 	12R 	14,000¥ 	SR5:R5.0",
        "Medium 	Evo Krokodil 	3 	2G/3W 	1 	3(1) 	6 	2 	2 	- 	8R 	12,000¥ 	SR5:R5.0",
        "Medium 	F-B Kull 	3 	4P 	2 	3(3) 	0 	3 	2 	- 	4 	10,000¥ 	SR5:R5.0",
        "Medium 	LEBD-2 	4 	2P 	1 	3(0) 	9 	4 	4 	- 	12R 	20,000¥ 	SR5:R5.0",
        "Medium 	Steed 	4/2 	1G 	1 	3(1) 	0 	2 	2 	- 	2 	4,000¥ 	SR5:R5.0",
        "Medium 	Tunneler 	3 	0P 	0 	3(2) 	6 	2 	2 	- 	8R 	10,000¥ 	SR5:R5.0"
)

List<String> DroneLargeList = Arrays.asList(
        "Large 	Cyberspace Designs Dalmation 	5 	5 	3 	5 	5 	3 	3 	- 	6R 	10,000¥ 	Core",
        "Large 	Steel Lynx Combat Drone 	5 	4 	2 	6 	12 	3 	3 	- 	10R 	25,000¥ 	Core",
        "Large 	Malakim 	3 	6P 	2 	4(0) 	9 	4 	4 	- 	20F 	40,000¥ 	SR5:R5.0",
        "Large 	Matilda 	1 	2G 	1 	8 	8 	2 	1 	- 	12R 	18,000¥ 	SR5:R5.0",
        "Large 	MediCart 	5 	5G 	1 	6(2) 	5 	4 	4 	- 	6 	10,000¥ 	SR5:R5.0",
        "Large 	Mule 	4 	1G 	1 	4(3) 	6 	2 	2 	- 	4 	8,000¥ 	SR5:R5.0",
        "Large 	Neptune 	2 	3W 	1 	5(0) 	3 	4 	3 	- 	10R 	17,500¥ 	SR5:R5.0",
        "Large 	Paladin 	5 	4G 	1 	5(0) 	18 	3 	2 	- 	8R 	5,000¥ 	SR5:R5.0",
        "Large 	Tower 	2 	1P 	1 	4(0) 	6 	2 	2 	- 	8 	10,000¥ 	SR5:R5.0",
)

List<String> DroneHugeList = Arrays.asList(
        "Huge 	Ares KN-Y0 (Deimos) 	3 	2G 	1 	6(0) 	18 	5 	3 	- 	20F 	220,000¥ 	SR5:R5.0",
        "Huge 	Ares KN-Y0 (Eris) 	3 	2G 	1 	6(0) 	18 	5 	3 	- 	24F 	270,000¥ 	SR5:R5.0",
        "Huge 	Ares KN-Y0 (Phobos) 	3 	2G 	1 	6(0) 	18 	5 	3 	- 	16F 	250,000¥ 	SR5:R5.0",
        "Huge 	Avenging Angel 	3 	6J 	2 	6(0) 	12 	6 	6 	- 	40F 	1,000,000¥ 	SR5:R5.0",
        "Huge 	Kodiak 	2/4 	2G 	1 	6(2) 	12 	2 	2 	- 	12R 	40,000¥ 	SR5:R5.0"
)

List<String> DroneAnthroList = Arrays.asList(
        "Anthro 	Criado Juan 	2 	2G 	1 	2 	0 	2 	2 	- 	2 	8,000¥ 	SR5:R5.0",
        "Anthro 	Direktionssekretar 	4 	4G 	2 	4 	3 	4 	4 	- 	12R 	40,000¥ 	SR5:R5.0",
        "Anthro 	Juggernaught 	3 	4G 	1 	6 	12 	3 	3 	- 	14R 	100,000¥ 	SR5:R5.0",
        "Anthro 	Kenchiku-Kikai 	2 	2G 	1 	5 	3 	2 	2 	- 	8R 	20,000¥ 	SR5:R5.0",
        "Anthro 	Little Buddy 	2 	1G 	1 	1 	0 	2 	2 	- 	4 	2,000¥ 	SR5:R5.0",
        "Anthro 	Shiawase i-Doll 	3 	3G 	1 	3 	0 	3 	3 	- 	4 	20,000¥ 	SR5:R5.0"
)

String result = StringUtils.EMPTY
boolean drone = true
for (String line : DroneAnthroList) {

    Matcher m
    if (drone) {
        m = patterDrone.matcher(line)
    } else {
        m = patternVehicle.matcher(line)
    }


    if (!m.matches())
        println(line + " Doesnt match")

    String name = ""
    int handlingOnRoad = 0
    int handlingOffRoad = 0
    int speedOnRoad = 0
    int speedOffRoad = 0
    int accelerationOnRoad = 0
    int accelerationOffRoad = 0
    int body = 0
    int armor = 0
    int pilot = 0
    int sensor = 0

    if (m.group(1).contains("/")) {
        String[] value = m.group(1).split("/")
        handlingOnRoad = Integer.parseInt(value[0])
        handlingOffRoad = Integer.parseInt(value[1])
    } else {
        handlingOffRoad = Integer.parseInt(m.group(1))
        handlingOnRoad = handlingOffRoad
    }

    String speed = m.group(2).replaceAll("[A-Z]", "")

    if (speed.contains("/")) {
        String[] value = speed.split("/")
        speedOnRoad = Integer.parseInt(value[0])
        speedOffRoad = Integer.parseInt(value[1])
    } else {
        speedOnRoad = Integer.parseInt(speed)
        speedOffRoad = speedOnRoad
    }

    if (m.group(3).contains("/")) {
        String[] value = m.group(3).split("/")
        accelerationOnRoad = Integer.parseInt(value[0])
        accelerationOffRoad = Integer.parseInt(value[1])
    } else {
        accelerationOnRoad = Integer.parseInt(m.group(3))
        accelerationOffRoad = accelerationOnRoad
    }


    body = Integer.parseInt(m.group(4).replaceAll("\\([0-9]+\\)", ""))
    armor = Integer.parseInt(m.group(5))
    pilot = Integer.parseInt(m.group(6))
    sensor = Integer.parseInt(m.group(7))

    StringBuilder stringBuilder = new StringBuilder();

    stringBuilder.append("{\n" +
            "                  \"vehicle\": {")
    stringBuilder.append('"name": "",\n')
    stringBuilder.append('"handling": {\n' +
            '"onRoad": ' + handlingOnRoad + ', \n' +
            '"offRoad":' + handlingOffRoad +
            "},\n")
    stringBuilder.append('"speed": {\n' +
            '"onRoad": ' + speedOnRoad + ', \n' +
            '"offRoad":' + speedOffRoad +
            "},\n")
    stringBuilder.append('"acceleration": {\n' +
            '"onRoad": ' + accelerationOnRoad + ', \n' +
            '"offRoad":' + accelerationOffRoad +
            "},\n")
    stringBuilder.append('"body": ' + body + ',\n')
    stringBuilder.append('"armor": ' + armor + ',\n')
    stringBuilder.append('"pilot": ' + pilot + ',\n')
    stringBuilder.append('"sensor": ' + sensor + ',\n')
    stringBuilder.append('"condition": null,\n')
    stringBuilder.append("},\n" +
            "                  \"children\": [\n" +
            "                  ]\n" +
            "                }")

    result += stringBuilder.toString() + ",\n";
}

result