// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.players;

import com.wurmonline.server.MiscConstants;

public final class Titles implements MiscConstants
{
    public static final double SKILL_REQ_LEGENDARY = 99.99999615;
    
    public enum TitleType
    {
        NORMAL(0), 
        MINOR(1), 
        MASTER(2), 
        LEGENDARY(3), 
        EPIC(4);
        
        public final int id;
        
        private TitleType(final int _id) {
            this.id = _id;
        }
    }
    
    public enum Title
    {
        Miner_Master(1, "Mastermine", 1008, TitleType.MASTER), 
        Miner(2, "Miner", 1008, TitleType.NORMAL), 
        Woodcutter(3, "Lumberjack", 1007, TitleType.NORMAL), 
        Woodcutter_Master(4, "Deforester", 1007, TitleType.MASTER), 
        Herbalist(5, "Herbalist", 10072, TitleType.NORMAL), 
        Herbalist_Master(6, "Loremaster", 10072, TitleType.MASTER), 
        Alchemist(7, "Apothecarist", 10042, TitleType.NORMAL), 
        Alchemist_Master(8, "Rainbow Maker", 10042, TitleType.MASTER), 
        Mason(9, "Mason", 1013, TitleType.NORMAL), 
        Mason_Master(10, "Master Mason", 1013, TitleType.MASTER), 
        Potterer(11, "Potter", 1011, TitleType.NORMAL), 
        Potterer_Master(12, "Master Potter", "Terracotta Terror", 1011, TitleType.MASTER), 
        Channeler(13, "Channeler", 10067, TitleType.NORMAL), 
        Channeler_Master(14, "Casting Specialist", 10067, TitleType.MASTER), 
        Cleric(15, "Cleric", TitleType.NORMAL), 
        Cleric_Minor(16, "Priest", "Priestess", TitleType.MINOR), 
        Cleric_Master(17, "High Priest", "High Priestess", TitleType.MASTER), 
        Warrior(18, "Warrior", "Warmaid", TitleType.NORMAL), 
        Warrior_Minor(19, "Battlemaster", "Battlemaiden", TitleType.MINOR), 
        Warrior_Master(20, "Warlord", "Valkyrie", TitleType.MASTER), 
        DragonSlayer(21, "Dragonslayer"), 
        ArtifactFinder(22, "Explorer"), 
        Carpenter(23, "Carpenter", 1005, TitleType.NORMAL), 
        Carpenter_Master(24, "Master Carpenter", 1005, TitleType.MASTER), 
        Smithing_Black(25, "Blacksmith", 10015, TitleType.NORMAL), 
        Smithing_Black_Master(26, "Master Blacksmith", 10015, TitleType.MASTER), 
        Smithing_GoldSmith(27, "Goldsmith", 10043, TitleType.NORMAL), 
        Smithing_GoldSmith_Master(28, "Midas Touch", 10043, TitleType.MASTER), 
        Smithing_Lock(29, "Locksmith", 10034, TitleType.NORMAL), 
        Smithing_Lock_Master(30, "Master Locksmith", 10034, TitleType.MASTER), 
        Smithing_Armour(31, "Armoursmith", 1017, TitleType.NORMAL), 
        Smithing_Armour_Master(32, "Master Armourer", 1017, TitleType.MASTER), 
        Smithing_Weapons(33, "Weaponsmith", 1016, TitleType.NORMAL), 
        Smithing_Weapons_Master(34, "Master Weaponsmith", 1016, TitleType.MASTER), 
        Carpenter_Fine(35, "Fine Carpenter", 10044, TitleType.NORMAL), 
        Carpenter_Fine_Master(36, "Mighty Fine Carpenter", 10044, TitleType.MASTER), 
        Paving(37, "Roadbuilder", 10031, TitleType.NORMAL), 
        Paving_Master(38, "Road Warrior", 10031, TitleType.MASTER), 
        Prospecting(39, "Prospector", 10032, TitleType.NORMAL), 
        Prospecting_Master(40, "Goldsniffer", 10032, TitleType.MASTER), 
        Exorcism(41, "Exorcist", 10068, TitleType.NORMAL), 
        Exorcism_Master(42, "Terrorwringer", 10068, TitleType.MASTER), 
        Farmer(43, "Farmer", 10049, TitleType.NORMAL), 
        Farmer_Master(44, "Master Farmer", 10049, TitleType.MASTER), 
        Thief(45, "Pilferer", 1028, TitleType.NORMAL), 
        Thief_Minor(46, "Thief", 1028, TitleType.MINOR), 
        Thief_Master(47, "Master Thief", 1028, TitleType.MASTER), 
        Butcherer(48, "Butcher", 10059, TitleType.NORMAL), 
        Butcherer_Master(49, "Master Butcher", 10059, TitleType.MASTER), 
        Toymaker_Master(50, "Master Toymaker", 10051, TitleType.MASTER), 
        Toymaker(51, "Toymaker", 10051, TitleType.NORMAL), 
        Miller(52, "Miller", 10040, TitleType.NORMAL), 
        Miller_Master(53, "Master Miller", 10040, TitleType.MASTER), 
        Digging(54, "Digger", 1009, TitleType.NORMAL), 
        Digging_Master(55, "Land Shaper", 1009, TitleType.MASTER), 
        GiantSlayer(56, "Giantslayer"), 
        TrollSlayer(57, "Trollslayer"), 
        Cook(58, "Cook", 1018, TitleType.NORMAL), 
        Cook_Minor(59, "Chef", 1018, TitleType.MINOR), 
        Cook_Master(60, "Master Chef", 1018, TitleType.MASTER), 
        Mason_Minor(61, "High Mason", 1013, TitleType.MINOR), 
        Tailor(62, "Tailor", 10016, TitleType.NORMAL), 
        Tailor_Master(63, "Seamster", "Seamstress", 10016, TitleType.MASTER), 
        AnimalTrainer(64, "Animal Trainer", 10078, TitleType.NORMAL), 
        AnimalTrainer_Master(65, "Beastmaster", 10078, TitleType.MASTER), 
        Archer(66, "Archer", 1030, TitleType.NORMAL), 
        Archer_Minor(67, "Hawk Eye", 1030, TitleType.MINOR), 
        Archer_Master(68, "Eagle Eye", 1030, TitleType.MASTER), 
        Fletcher(69, "Fletcher", 1032, TitleType.NORMAL), 
        Fletcher_Master(70, "Master Fletcher", 1032, TitleType.MASTER), 
        Bowmaker(71, "Bowmaker", 1031, TitleType.NORMAL), 
        Bowmaker_Master(72, "Master Bowmaker", 1031, TitleType.MASTER), 
        Smith(73, "Smith", 1015, TitleType.NORMAL), 
        Smith_Minor(74, "Tinker", 1015, TitleType.MINOR), 
        Smith_Master(75, "Master Smith", 1015, TitleType.MASTER), 
        Repairer(76, "Repairman", "Repairwoman", 10035, TitleType.NORMAL), 
        Repairer_Minor(77, "Materia Tamer", 10035, TitleType.MINOR), 
        Repairer_Master(78, "Master Fixer", 10035, TitleType.MASTER), 
        Tracker(79, "Tracker", 10018, TitleType.NORMAL), 
        Tracker_Minor(80, "Pathfinder", 10018, TitleType.MINOR), 
        Tracker_Master(81, "Master Pathfinder", 10018, TitleType.MASTER), 
        Ageless(82, "Ageless"), 
        Fisher(83, "Fisherman", "Fisherwoman", 10033, TitleType.NORMAL), 
        Fisher_Minor(84, "Angler", 10033, TitleType.MINOR), 
        Fisher_Master(85, "Kingfisher", 10033, TitleType.MASTER), 
        Climber(86, "Mountaineer", 10073, TitleType.NORMAL), 
        Climber_Master(87, "Cliffhanger", 10073, TitleType.MASTER), 
        Firemaker(88, "Pyrotechnic", 1010, TitleType.NORMAL), 
        Firemaker_Master(89, "Pyromaniac", 1010, TitleType.MASTER), 
        Misc(90, "Handyman", "Handywoman", 1020, TitleType.NORMAL), 
        Misc_Master(91, "Fixer", 1020, TitleType.MASTER), 
        Nature(92, "Ecologist", 1019, TitleType.NORMAL), 
        Nature_Master(93, "Druidkin", 1019, TitleType.MASTER), 
        KeeperTruth(94, "Keeper of Truth"), 
        Praying(95, "Reverent", 10066, TitleType.NORMAL), 
        Praying_Master(96, "Ascetic", 10066, TitleType.MASTER), 
        Leather(97, "Tanner", 10017, TitleType.NORMAL), 
        Leather_Master(98, "Master Tanner", 10017, TitleType.MASTER), 
        Drunkard(99, "Drunkard", TitleType.MINOR), 
        Alcoholic(100, "Alcoholic", TitleType.MINOR), 
        Firemaker_Minor(101, "Arsonist", 1010, TitleType.MINOR), 
        Woodcutter_Minor(102, "Timberman", "Timberwoman", 1007, TitleType.MINOR), 
        Leather_Minor(103, "High Tanner", 10017, TitleType.MINOR), 
        Smithing_Black_Minor(104, "Renowned Blacksmith", 10015, TitleType.MINOR), 
        Digging_Minor(105, "Excavator", 1009, TitleType.MINOR), 
        Miner_Minor(106, "Prime Minester", 1008, TitleType.MINOR), 
        Ropemaking(107, "Ropemaker", 1014, TitleType.NORMAL), 
        Ropemaking_Minor(108, "Renowned Ropemaker", 1014, TitleType.MINOR), 
        Ropemaking_Master(109, "Hangman", "Hangwoman", 1014, TitleType.MASTER), 
        Coaler(110, "Coaler", 10036, TitleType.NORMAL), 
        Coaler_Master(111, "Master Coaler", 10036, TitleType.MASTER), 
        Carpenter_Fine_Minor(112, "Renowned Fine Carpenter", 10044, TitleType.MINOR), 
        Carpenter_Minor(113, "Renowned Carpenter", 1005, TitleType.MINOR), 
        Keeper_Faith(114, "Keeper of Faith"), 
        Destroyer_Faith(115, "Destroyer of Faith"), 
        Smithing_ShieldSmith(116, "Shieldsmith", 10014, TitleType.NORMAL), 
        Smithing_ShieldSmith_Master(117, "Master Shieldsmith", 10014, TitleType.MASTER), 
        One(118, "The Unforgiven King"), 
        HFC(119, "Caterer", 10038, TitleType.NORMAL), 
        HFC_Master(120, "Saucier", 10038, TitleType.MASTER), 
        Smithing_GoldSmith_Minor(121, "Renowned Goldsmith", 10043, TitleType.MINOR), 
        Firstaid(122, "Quacksalver", 10056, TitleType.NORMAL), 
        Firstaid_Minor(123, "Medic", 10056, TitleType.MINOR), 
        Firstaid_Master(124, "Paramedic", 10056, TitleType.MASTER), 
        Yoyo(125, "Trickster", 10050, TitleType.NORMAL), 
        Yoyo_Minor(126, "Freestyler", 10050, TitleType.MINOR), 
        Yoyo_Master(127, "Wild One", 10050, TitleType.MASTER), 
        Stonecut(128, "Sculptor", 10074, TitleType.NORMAL), 
        Stonecut_Minor(129, "Artist", 10074, TitleType.MINOR), 
        Stonecut_Master(130, "Michelangelo", 10074, TitleType.MASTER), 
        UniqueSlayer(131, "Fearless"), 
        Smithing_ShieldSmith_Minor(132, "Able Shieldsmith", 10014, TitleType.MINOR), 
        Farmer_Minor(133, "Crofter", 10049, TitleType.MINOR), 
        Platesmith(134, "Platesmith", 10013, TitleType.NORMAL), 
        Platesmith_Minor(135, "Renowned Platesmith", 10013, TitleType.MINOR), 
        Platesmith_Master(136, "Master Platesmith", 10013, TitleType.MASTER), 
        Chainsmith(137, "Chainsmith", 10012, TitleType.NORMAL), 
        Chainsmith_Minor(138, "Renowned Chainsmith", 10012, TitleType.MINOR), 
        Chainsmith_Master(139, "Master Chainsmith", 10012, TitleType.MASTER), 
        Kingdomtitle(140, "Kingdom Title", -1, TitleType.MASTER), 
        Smithing_Weapons_minor(141, "Arms Master", 1016, TitleType.MINOR), 
        Renowned_Bard(142, "Renowned Bard"), 
        Kingslayer(143, "Kingslayer"), 
        Prospecting_Minor(144, "Geologist", 10032, TitleType.MINOR), 
        Forester(145, "Forester", 10048, TitleType.NORMAL), 
        Forester_Master(146, "Arbophiliac", 10048, TitleType.MASTER), 
        Forester_Minor(147, "Arboriculturist", 10048, TitleType.MINOR), 
        Shipbuilder(148, "Harbormaster", 10082, TitleType.NORMAL), 
        Shipbuilder_Master(149, "Master Shipwright", 10082, TitleType.MASTER), 
        Shipbuilder_Minor(150, "Shipwright", 10082, TitleType.MINOR), 
        Bowyer_Minor(151, "Able Bowmaker", 1031, TitleType.MINOR), 
        Fletcher_Minor(152, "Arrowmaker", 1032, TitleType.MINOR), 
        Knifer(153, "Stabber", 10007, TitleType.NORMAL), 
        Misc_Minor(154, "Tim the Toolman", "Tim the Toolwoman", 1020, TitleType.MINOR), 
        Exor_Minor(155, "Zealot", 10068, TitleType.MINOR), 
        Tailoring(156, "Textiler", 1012, TitleType.NORMAL), 
        Tailoring_Minor(157, "Needlefriend", 1012, TitleType.MINOR), 
        Tailoring_Master(158, "Velcro Fly", 1012, TitleType.MASTER), 
        Swords(159, "Swordsman", "Swordswoman", 1000, TitleType.NORMAL), 
        Swords_Minor(160, "Duelist", 1000, TitleType.MINOR), 
        Swords_Master(161, "Swordsmaster", "Swordsmistress", 1000, TitleType.MASTER), 
        Axes(162, "Axeman", "Axewoman", 1003, TitleType.NORMAL), 
        Axes_Minor(163, "Berserker", 1003, TitleType.MINOR), 
        Axes_Master(164, "Fleshrender", 1003, TitleType.MASTER), 
        Mauls(165, "Mauler", 1004, TitleType.NORMAL), 
        Mauls_Minor(166, "Bonker", 1004, TitleType.MINOR), 
        Mauls_Master(167, "Crusher", 1004, TitleType.MASTER), 
        Soldier(168, "Soldier", 1023, TitleType.NORMAL), 
        Soldier_Minor(169, "Mercenary", 1023, TitleType.MINOR), 
        Soldier_Master(170, "Knight", "Amazon", 1023, TitleType.MASTER), 
        Shields(171, "Shieldsman", "Shieldwoman", 1002, TitleType.NORMAL), 
        Shields_Minor(172, "Defender", "Defendress", 1002, TitleType.MINOR), 
        Shields_Master(173, "Protector", 1002, TitleType.MASTER), 
        Warmachines(174, "Destroyer", 1029, TitleType.NORMAL), 
        Warmachines_Minor(175, "Demolisher", 1029, TitleType.MINOR), 
        Warmachines_Master(176, "Siege Master", "Siege Mistress", 1029, TitleType.MASTER), 
        Botanist_Minor(177, "Botanist", 10072, TitleType.MINOR), 
        Nature_Minor(178, "Treehugger", 1019, TitleType.MINOR), 
        Pottery_Minor(179, "Moulder", 1011, TitleType.MINOR), 
        Cooking_Minor(180, "Iron Chef", 10038, TitleType.MINOR), 
        Channeling_Minor(181, "Conduit", 10067, TitleType.MINOR), 
        Faith(182, "Padre", "Norn", TitleType.NORMAL), 
        Faith_Minor(183, "Devout", "Mystic", TitleType.MINOR), 
        Faith_Master(184, "Enlightened", TitleType.MASTER), 
        Taming_Minor(185, "Beastspeaker", 10078, TitleType.MINOR), 
        Body(186, "Athlete", 1, TitleType.NORMAL), 
        Butcher_Minor(187, "Der Metzgermeister", "Skinner", 10059, TitleType.MINOR), 
        Beverages(188, "Bartender", 10083, TitleType.NORMAL), 
        Beverages_Minor(189, "Party Animal", 10083, TitleType.MINOR), 
        Beverages_Master(190, "Sommelier", 10083, TitleType.MASTER), 
        PA(191, "Community Assistant", TitleType.MASTER), 
        Milking(192, "Milkman", "Milkmaid", 10060, TitleType.NORMAL), 
        Meditation(193, "Guru", 10086, TitleType.NORMAL), 
        Meditation_Minor(194, "Swami", 10086, TitleType.MINOR), 
        Meditation_Master(195, "Rama", 10086, TitleType.MASTER), 
        Alchemist_Minor(196, "Transmutator", 10042, TitleType.MINOR), 
        Tailor_Cloth_Minor(197, "Dreamweaver", 10016, TitleType.MINOR), 
        AnimalHusbandry(198, "Drover", 10085, TitleType.NORMAL), 
        AnimalHusbandry_Minor(199, "Granger", 10085, TitleType.MINOR), 
        AnimalHusbandry_Master(200, "Rancher", 10085, TitleType.MASTER), 
        Puppeteering(201, "Puppeteer", 10087, TitleType.NORMAL), 
        Puppeteering_Minor(202, "Entertainer", 10087, TitleType.MINOR), 
        Puppeteering_Master(203, "Puppetmaster", 10087, TitleType.MASTER), 
        Saint(204, "Saint"), 
        ChampSlayer(205, "Slayer of Champions"), 
        Baker(206, "Boulanger", 10039, TitleType.NORMAL), 
        Baker_Minor(207, "Bake Sale", 10039, TitleType.MINOR), 
        Baker_Master(208, "Confissier", 10039, TitleType.MASTER), 
        First_Digger(209, "First digger of Wurm", TitleType.MASTER), 
        Gardener(210, "Greenthumbs", 10045, TitleType.NORMAL), 
        Gardener_Minor(211, "Gardener", 10045, TitleType.MINOR), 
        Gardener_Master(212, "Jiko Enkai Zenji", 10045, TitleType.MASTER), 
        Lockpicking(213, "Lock Breaker", 10076, TitleType.NORMAL), 
        Lockpicking_Minor(214, "Safe Cracker", 10076, TitleType.MINOR), 
        Lockpicking_Master(215, "Vault Shadow", 10076, TitleType.MASTER), 
        Godsent(216, "Godsent"), 
        Educated(217, "Educated"), 
        Clairvoyant(218, "Clairvoyant"), 
        Smithing_Armor_minor(219, "Renowned Armourer", 1017, TitleType.MINOR), 
        Champ_Previous(220, "Champion Emeritus"), 
        Catapult(221, "Rock Hurler", 10077, TitleType.NORMAL), 
        Catapult_Minor(222, "Wall Breaker", 10077, TitleType.MINOR), 
        Catapult_Master(223, "Eradicator", 10077, TitleType.MASTER), 
        Altar_Destroyer(224, "Dawn of Glory", TitleType.MASTER), 
        Paving_Minor(225, "Road Engineer", 10031, TitleType.MINOR), 
        Soul(226, "Soulman", 106, TitleType.NORMAL), 
        Coaler_Minor(227, "Renowned Coaler", 10036, TitleType.MINOR), 
        Body_Minor(228, "Body Builder", 1, TitleType.MINOR), 
        Body_Major(229, "Olympian", 1, TitleType.MASTER), 
        Secrets_Master(230, "Master of Secrets", "Mistress of secrets"), 
        Hota_One(231, "Participant of the Hunt"), 
        Hota_Two(232, "Bloodhound of the Hunt"), 
        Hota_Three(233, "Leader of the Hunt"), 
        Hota_Four(234, "Master of the Hunt"), 
        Hota_Five(235, "King of the Hunt"), 
        CM(236, "Chat Moderator", TitleType.MASTER), 
        Carving_Knife_Minor(237, "Carver", 10007, TitleType.MINOR), 
        Carving_Knife_Master(238, "Whittler", 10007, TitleType.MASTER), 
        Climbing_Minor(239, "Sherpa", 10073, TitleType.MINOR), 
        Knives(240, "Knifer", 1001, TitleType.NORMAL), 
        Knives_Minor(241, "Piercer", 1001, TitleType.MINOR), 
        Knives_Master(242, "Knife Specialist", 1001, TitleType.MASTER), 
        Metallurgy(243, "Smelter", 10041, TitleType.NORMAL), 
        Metallurgy_Minor(244, "Metallurgist", 10041, TitleType.MINOR), 
        Metallurgy_Master(245, "Master Metallurgist", 10041, TitleType.MASTER), 
        Toymaking_Minor(246, "Gepetto", 10051, TitleType.MINOR), 
        Locksmithing_Minor(247, "Safe Smith", 10034, TitleType.MINOR), 
        First_Crafter(248, "First Crafter of Wurm", TitleType.MASTER), 
        Taunter_Master(249, "Loathsome", 10057, TitleType.MASTER), 
        Taunter(250, "Annoying", 10057, TitleType.NORMAL), 
        Taunter_Minor(251, "Repulsive", 10057, TitleType.MINOR), 
        Winner(252, "Winner", TitleType.MASTER), 
        UnholyAlly(253, "Unholy Ally", TitleType.MASTER), 
        SoldierLomaner(254, "Soldier of Lomaner"), 
        RiderLomaner(255, "Rider of Lomaner"), 
        ChieftainLomaner(256, "Chieftain of Lomaner"), 
        AmbassadorLomaner(257, "Ambassador of Lomaner"), 
        BaronLomaner(258, "Baron of Lomaner", "Baroness of Lomaner"), 
        JarlLomaner(259, "Jarl of Lomaner", "Countess of Lomaner"), 
        DukeLomaner(260, "Duke of Lomaner", "Duchess of Lomaner"), 
        ProvostLomaner(261, "Provost of Lomaner"), 
        MarquisLomaner(262, "Marquis of Lomaner", "Marchioness of Lomaner"), 
        GrandDukeLomaner(263, "Grand Duke of Lomaner", "Grand Duchess of Lomaner"), 
        ViceRoyLomaner(264, "ViceRoy of Lomaner"), 
        PrinceLomaner(265, "Prince of Lomaner", "Princess of Lomaner"), 
        Puppeteering_Legend(266, "First Jester of Wurm", 10087, TitleType.LEGENDARY), 
        Digger_Legend(267, "Earthwurm", 1009, TitleType.LEGENDARY), 
        Tracker_Legend(268, "Bloodhound of Wurm", 10018, TitleType.LEGENDARY), 
        Yoyo_Legend(269, "Legendary Woodspinner", 10050, TitleType.LEGENDARY), 
        Farmer_Legend(270, "Pumpkin King", 10049, TitleType.LEGENDARY), 
        Mining_Legend(271, "The World is Mine", 1008, TitleType.LEGENDARY), 
        Fishing_Legend(272, "Hooked on Fishing", 10033, TitleType.LEGENDARY), 
        Hotfood_Legend(273, "Legendary Chef", 10038, TitleType.LEGENDARY), 
        Fighting_Legend(274, "Swift Death", 1023, TitleType.LEGENDARY), 
        Foraging(275, "Forager", 10071, TitleType.NORMAL), 
        Foraging_Minor(276, "Plant Gatherer", 10071, TitleType.MINOR), 
        Foraging_Master(277, "Master of Plants", 10071, TitleType.MASTER), 
        Mind(278, "Thinker", 2, TitleType.NORMAL), 
        Mind_Minor(279, "Scholar", 2, TitleType.MINOR), 
        Mind_Master(280, "Philosopher", 2, TitleType.MASTER), 
        Soul_Normal(281, "Spiritualist", 3, TitleType.NORMAL), 
        Soul_Minor(282, "Visionary", 3, TitleType.MINOR), 
        Soul_Master(283, "Maverick", 3, TitleType.MASTER), 
        Alchemy_Normal(284, "Alchemist", 1021, TitleType.NORMAL), 
        Alchemy_Minor(285, "Chemist", 1021, TitleType.MINOR), 
        Alchemy_Master(286, "Shaman", 1021, TitleType.MASTER), 
        Clubs_Normal(287, "Clubber", 1025, TitleType.NORMAL), 
        Clubs_Minor(288, "Thumper", 1025, TitleType.MINOR), 
        Clubs_Master(289, "Smasher", 1025, TitleType.MASTER), 
        Dairy_Normal(290, "Cheesemaker", 10037, TitleType.NORMAL), 
        Dairy_Minor(291, "Artisan Cheesemaker", 10037, TitleType.MINOR), 
        Dairy_Master(292, "Master Cheesemaker", 10037, TitleType.MASTER), 
        Hammers_Normal(293, "Hammerer", 1027, TitleType.NORMAL), 
        Hammers_Minor(294, "Assaulter", 1027, TitleType.MINOR), 
        Hammers_Master(295, "Thor", 1027, TitleType.MASTER), 
        Healing_Normal(296, "Healer", 1024, TitleType.NORMAL), 
        Healing_Minor(297, "Doctor", 1024, TitleType.MINOR), 
        Healing_Master(298, "Surgeon", 1024, TitleType.MASTER), 
        Milling_Minor(299, "Grain Expert", 10040, TitleType.MINOR), 
        Milking_Minor(300, "Milking Machine", 10060, TitleType.MINOR), 
        Milking_Master(301, "Cow Whisperer", 10060, TitleType.MASTER), 
        Papyrus_Normal(302, "Papermaker", 10091, TitleType.NORMAL), 
        Papyrus_Minor(303, "Pulp-beater", 10091, TitleType.MINOR), 
        Papyrus_Master(304, "Book Maker", 10091, TitleType.MASTER), 
        Polearms_Normal(305, "Lancer", 1033, TitleType.NORMAL), 
        Polearms_Minor(306, "Phalanx", 1033, TitleType.MINOR), 
        Polearms_Master(307, "Impaler", 1033, TitleType.MASTER), 
        Religion_Normal(308, "Spiritual", 1026, TitleType.NORMAL), 
        Religion_Minor(309, "Theological", 1026, TitleType.MINOR), 
        Religion_Master(310, "Zealous", 1026, TitleType.MASTER), 
        Archaeology_Normal(311, "Investigator", 10069, TitleType.NORMAL), 
        Archaeology_Minor(312, "Archaeologist", 10069, TitleType.MINOR), 
        Archaeology_Master(313, "Curator", 10069, TitleType.MASTER), 
        Prayer_Minor(314, "Pious", 10066, TitleType.MINOR), 
        Preaching_Normal(315, "Preacher", 10065, TitleType.NORMAL), 
        Preaching_Minor(316, "Orator", 10065, TitleType.MINOR), 
        Preaching_Master(317, "Soothsayer", 10065, TitleType.MASTER), 
        Thatching_Normal(318, "Thatcher", 10092, TitleType.NORMAL), 
        Thatching_Minor(319, "Roofer", 10092, TitleType.MINOR), 
        Thatching_Master(320, "Master Thatcher", 10092, TitleType.MASTER), 
        Stealing_Normal(321, "Pickpocket", 10075, TitleType.NORMAL), 
        Stealing_Minor(322, "Burglar", 10075, TitleType.MINOR), 
        Stealing_Master(323, "Robber", 10075, TitleType.MASTER), 
        Traps_Normal(324, "It's a Trap!", 10084, TitleType.NORMAL), 
        Traps_Minor(325, "Trapper", 10084, TitleType.MINOR), 
        Traps_Master(326, "Snare Artist", 10084, TitleType.MASTER), 
        Toys_Normal(327, "Performer", 1022, TitleType.NORMAL), 
        Toys_Minor(328, "Manipulator", 1022, TitleType.MINOR), 
        Toys_Master(329, "Thespian", 1022, TitleType.MASTER), 
        AnimalHusbandry_Legend(330, "Zoologist", 10085, TitleType.LEGENDARY), 
        Faith_Legend(331, "Illuminated", 2147483645, TitleType.LEGENDARY), 
        Paving_Legend(332, "World Connector", 10031, TitleType.LEGENDARY), 
        Stealing_Legend(333, "Criminal Mastermind", 10075, TitleType.LEGENDARY), 
        Woodcutting_Legend(334, "Wurmian Termite", 1007, TitleType.LEGENDARY), 
        Milking_Legend(335, "Udder Madness", 10060, TitleType.LEGENDARY), 
        Puppeteering_Epic(336, "Epic Puppeteer", 10087, TitleType.EPIC), 
        Digger_Epic(337, "Epic Digger", 1009, TitleType.EPIC), 
        Tracker_Epic(338, "Epic Tracker", 10018, TitleType.EPIC), 
        Yoyo_Epic(339, "Epic Woodspinner", 10050, TitleType.EPIC), 
        Farmer_Epic(340, "Epic Farmer", 10049, TitleType.EPIC), 
        Mining_Epic(341, "Epic Miner", 1008, TitleType.EPIC), 
        Fishing_Epic(342, "Epic Fisherman", "Epic Fisherwoman", 10033, TitleType.EPIC), 
        Hotfood_Epic(343, "Epic Chef", 10038, TitleType.EPIC), 
        Fighting_Epic(344, "Epic Fighter", 1023, TitleType.EPIC), 
        AnimalHusbandry_Epic(345, "Epic Animal Tamer", 10085, TitleType.EPIC), 
        Paving_Epic(346, "Epic Paver", 10031, TitleType.EPIC), 
        Stealing_Epic(347, "Epic Thief", 10075, TitleType.EPIC), 
        Woodcutting_Epic(348, "Epic Lumberjack", 1007, TitleType.EPIC), 
        Milking_Epic(349, "Epic Milkman", "Epic Milkmaid", 10060, TitleType.EPIC), 
        Carpenter_Legend(350, "Legendary Architect", 1005, TitleType.LEGENDARY), 
        Carpenter_Epic(351, "Epic Architect", 1005, TitleType.EPIC), 
        Saw_Normal(352, "I'm Sawry", 10008, TitleType.NORMAL), 
        Saw_Minor(353, "Chainsaw", 10008, TitleType.MINOR), 
        Saw_Master(354, "Jigsaw", 10008, TitleType.MASTER), 
        Saw_Legendary(355, "Tom Sawyer", 10008, TitleType.LEGENDARY), 
        Saw_Epic(356, "Epic Sawyer", 10008, TitleType.EPIC), 
        Master_of_Challenge(357, "Master of the Challenge", "Mistress of the Challenge"), 
        Lord_of_Isles(358, "Lord of the Isles", "Lady of the Isles"), 
        Blood_Ravager(359, "Blood Ravager"), 
        Selfie(360, "Selfie"), 
        Pit_Slayer(361, "Pit Slayer"), 
        Guardian_of_the_Hunt(362, "Guardian of the Hunt"), 
        Shadow_Assassin(363, "Shadow Assassin"), 
        Future_Warden(364, "Future Warden"), 
        Emperor_Emeritus(365, "Emperor Emeritus"), 
        Pack_of_the_Hunt(366, "Pack of the Hunt"), 
        Leather_Legend(367, "Master of the Hide", "Mistress of the Hide", 10017, TitleType.LEGENDARY), 
        Leather_Epic(368, "Epic Tanner", 10017, TitleType.EPIC), 
        Knigt(369, "Cavalier"), 
        FallenKnight(370, "Fallen Cavalier"), 
        Blacksmith_Legend(371, "Anvil of Tears", 10015, TitleType.LEGENDARY), 
        Blacksmith_Epic(372, "Epic Blacksmith", 10015, TitleType.EPIC), 
        Rifter(373, "Rifter"), 
        RiftDefender(374, "Rift Defender"), 
        Fletcher_Legend(375, "Quivering with Excitement", 1032, TitleType.LEGENDARY), 
        Fletcher_Epic(376, "Epic Fletcher", 1032, TitleType.EPIC), 
        Chainsmith_Legend(377, "Chains of Time", 10012, TitleType.LEGENDARY), 
        Chainsmith_Epic(378, "Epic Chainsmith", 10012, TitleType.EPIC), 
        Platesmith_Epic(379, "Imperial Dragonsmith", 10013, TitleType.LEGENDARY), 
        Bowyry_Legend(380, "Bow Down", 1031, TitleType.LEGENDARY), 
        Smithing_ShieldSmith_Legend(381, "Forge of the Shieldwall", 10014, TitleType.LEGENDARY), 
        Smithing_ShieldSmith_Epic(382, "Epic Shieldsmith", 10014, TitleType.EPIC), 
        Beverages_Legend(383, "Cocktail Shaker", 10083, TitleType.LEGENDARY), 
        Beverages_Epic(384, "Cocktail Shaker", 10083, TitleType.EPIC), 
        Restoration_Normal(385, "Restorer", 10095, TitleType.NORMAL), 
        Restoration_Minor(386, "Fragmented", 10095, TitleType.MINOR), 
        Restoration_Master(387, "Conservator", 10095, TitleType.MASTER), 
        Masonry_Legend(388, "Another Brick In The Wall", 1013, TitleType.LEGENDARY), 
        Masonry_Epic(389, "Another Brick In The Wall", 1013, TitleType.EPIC), 
        Prospecting_Legend(390, "I am so Vein", 10032, TitleType.LEGENDARY), 
        Aggressivefighting_Normal(391, "Feisty", 10053, TitleType.NORMAL), 
        Aggressivefighting_Minor(392, "Belligerent", 10053, TitleType.MINOR), 
        Aggressivefighting_Master(393, "Barbarian", 10053, TitleType.MASTER), 
        BodyControl_Normal(394, "Controlled", 104, TitleType.NORMAL), 
        BodyControl_Minor(395, "Disciplined", 104, TitleType.MINOR), 
        BodyControl_Master(396, "Acrobat", 104, TitleType.MASTER), 
        Bodystamina_Normal(397, "Enduring", 103, TitleType.NORMAL), 
        Bodystamina_Minor(398, "Perseverent", 103, TitleType.MINOR), 
        Bodystamina_Master(399, "Indefatigable", 103, TitleType.MASTER), 
        Bodystrength_Normal(400, "Robust", 102, TitleType.NORMAL), 
        Bodystrength_Minor(401, "Strongman", 102, TitleType.MINOR), 
        Bodystrength_Master(402, "Atlas", 102, TitleType.MASTER), 
        Butcheringknife_Normal(403, "Dissector", 10029, TitleType.NORMAL), 
        Butcheringknife_Minor(404, "Skinner", 10029, TitleType.MINOR), 
        Butcheringknife_Master(405, "Cleaver", 10029, TitleType.MASTER), 
        Defensivefighting_Normal(406, "Defensive", 10054, TitleType.NORMAL), 
        Defensivefighting_Minor(407, "Steadfast", 10054, TitleType.MINOR), 
        Defensivefighting_Master(408, "Guardian", 10054, TitleType.MASTER), 
        Halberd_Normal(409, "Pikeman", 10089, TitleType.NORMAL), 
        Halberd_Minor(410, "Halberdier", 10089, TitleType.MINOR), 
        Halberd_Master(411, "Man At Arms", "Woman At Arms", 10089, TitleType.MASTER), 
        Hammer_Normal(412, "Tapper", 10026, TitleType.NORMAL), 
        Hammer_Minor(413, "Knocker", 10026, TitleType.MINOR), 
        Hammer_Master(414, "Pounder", 10026, TitleType.MASTER), 
        Hatchet_Normal(415, "Woodsman", 10003, TitleType.NORMAL), 
        Hatchet_Minor(416, "Hewer", 10003, TitleType.MINOR), 
        Hatchet_Master(417, "Tomahawk", 10003, TitleType.MASTER), 
        Hugeaxe_Normal(418, "Huge Axeman", "Huge Axewoman", 10025, TitleType.NORMAL), 
        Hugeaxe_Minor(419, "Headsman", 10025, TitleType.MINOR), 
        Hugeaxe_Master(420, "Axecutioner", 10025, TitleType.MASTER), 
        HugeClub_Normal(421, "Troll", 10064, TitleType.NORMAL), 
        HugeClub_Minor(422, "Forest Giant", 10064, TitleType.MINOR), 
        HugeClub_Master(423, "Kyklops", 10064, TitleType.MASTER), 
        Largeaxe_Normal(424, "Large Axeman", "Large Axewoman", 10024, TitleType.NORMAL), 
        Largeaxe_Minor(425, "Battleaxe", 10024, TitleType.MINOR), 
        Largeaxe_Master(426, "Viking", 10024, TitleType.MASTER), 
        LargeMaul_Normal(427, "Large Mauler", 10061, TitleType.NORMAL), 
        LargeMaul_Minor(428, "Siegebreaker", 10061, TitleType.MINOR), 
        LargeMaul_Master(429, "Hand of Magranon", 10061, TitleType.MASTER), 
        Largemetalshield_Normal(430, "Sturdy Heavy Defender", 10023, TitleType.NORMAL), 
        Largemetalshield_Minor(431, "Sturdy Heavy Bastion", 10023, TitleType.MINOR), 
        Largemetalshield_Master(432, "Sturdy Heavy Bulwark", 10023, TitleType.MASTER), 
        Largewoodenshield_Normal(433, "Nimble Heavy Defender", 10021, TitleType.NORMAL), 
        Largewoodenshield_Minor(434, "Nimble Heavy Bastion", 10021, TitleType.MINOR), 
        Largewoodenshield_Master(435, "Nimble Heavy Bulwark", 10021, TitleType.MASTER), 
        Longbow_Normal(436, "Longbowman", 10081, TitleType.NORMAL), 
        Longbow_Minor(437, "Marksman", 10081, TitleType.MINOR), 
        Longbow_Master(438, "Robin Hood", 10081, TitleType.MASTER), 
        LongSpear_Normal(439, "Spearman", 10088, TitleType.NORMAL), 
        LongSpear_Minor(440, "Hussar", 10088, TitleType.MINOR), 
        LongSpear_Master(441, "Spartan", 10088, TitleType.MASTER), 
        Longsword_Normal(442, "Long Swordsman", 10005, TitleType.NORMAL), 
        Longsword_Minor(443, "Fighter", 10005, TitleType.MINOR), 
        Longsword_Master(444, "Myrmidon", 10005, TitleType.MASTER), 
        Mediumbow_Normal(445, "Bowman", 10080, TitleType.NORMAL), 
        Mediumbow_Minor(446, "Skirmisher", 10080, TitleType.MINOR), 
        Mediumbow_Master(447, "Ranger", 10080, TitleType.MASTER), 
        Mediummaul_Normal(448, "Medium Mauler", 10062, TitleType.NORMAL), 
        Mediummaul_Minor(449, "Whacker", 10062, TitleType.MINOR), 
        Mediummaul_Master(450, "Cracker", 10062, TitleType.MASTER), 
        Mediummetalshield_Normal(451, "Sturdy Defender", 10006, TitleType.NORMAL), 
        Mediummetalshield_Minor(452, "Sturdy Bastion", 10006, TitleType.MINOR), 
        Mediummetalshield_Master(453, "Sturdy Bulwark", 10006, TitleType.MASTER), 
        Mediumwoodenshield_Normal(454, "Nimble Defender", 10020, TitleType.NORMAL), 
        Mediumwoodenshield_Minor(455, "Nimble Bastion", 10020, TitleType.MINOR), 
        Mediumwoodenshield_Master(456, "Nimble Bulwark", 10020, TitleType.MASTER), 
        MindLogic_Normal(457, "Logician", 100, TitleType.NORMAL), 
        MindLogic_Minor(458, "Genius", 100, TitleType.MINOR), 
        MindLogic_Master(459, "Smarty Pants", 100, TitleType.MASTER), 
        MindSpeed_Normal(460, "Quick", 101, TitleType.NORMAL), 
        MindSpeed_Minor(461, "Witty", 101, TitleType.MINOR), 
        MindSpeed_Master(462, "Prodigy", 101, TitleType.MASTER), 
        Normalfighting_Normal(463, "Infantry", 10055, TitleType.NORMAL), 
        Normalfighting_Minor(464, "Sergeant", 10055, TitleType.MINOR), 
        Normalfighting_Master(465, "Captain", 10055, TitleType.MASTER), 
        Pickaxe_Normal(466, "Rockbreaker", 10009, TitleType.NORMAL), 
        Pickaxe_Minor(467, "Tunneller", 10009, TitleType.MINOR), 
        Pickaxe_Master(468, "Vein Destroyer", 10009, TitleType.MASTER), 
        Rake_Normal(469, "Cultivator", 10004, TitleType.NORMAL), 
        Rake_Minor(470, "Furrower", 10004, TitleType.MINOR), 
        Rake_Master(471, "Harrower", 10004, TitleType.MASTER), 
        Scythe_Normal(472, "Harvester", 10047, TitleType.NORMAL), 
        Scythe_Minor(473, "Reaper", 10047, TitleType.MINOR), 
        Scythe_Master(474, "Hand of Libila", 10047, TitleType.MASTER), 
        Shieldbashing_Normal(475, "Basher", 10058, TitleType.NORMAL), 
        Shieldbashing_Minor(476, "Stunner", 10058, TitleType.MINOR), 
        Shieldbashing_Master(477, "Juggernaut", 10058, TitleType.MASTER), 
        Shortbow_Normal(478, "Shortbowman", 10079, TitleType.NORMAL), 
        Shortbow_Minor(479, "Bowhunter", 10079, TitleType.MINOR), 
        Shortbow_Master(480, "Point Blank", 10079, TitleType.MASTER), 
        Shortsword_Normal(481, "Short Swordsman", "Short Swordswoman", 10027, TitleType.NORMAL), 
        Shortsword_Minor(482, "Gladiator", 10027, TitleType.MINOR), 
        Shortsword_Master(483, "Assassin", 10027, TitleType.MASTER), 
        Shovel_Normal(484, "Shoveler", 10002, TitleType.NORMAL), 
        Shovel_Minor(485, "Packer", 10002, TitleType.MINOR), 
        Shovel_Master(486, "Undertaker", 10002, TitleType.MASTER), 
        Sickle_Normal(487, "Pruner", 10046, TitleType.NORMAL), 
        Sickle_Minor(488, "Gatherer", 10046, TitleType.MINOR), 
        Sickle_Master(489, "Fully Sickle", 10046, TitleType.MASTER), 
        SmallAxe_Normal(490, "Small Axeman", "Small Axewoman", 10001, TitleType.NORMAL), 
        SmallAxe_Minor(491, "Hacker", 10001, TitleType.MINOR), 
        SmallAxe_Master(492, "Ripper", 10001, TitleType.MASTER), 
        Smallmaul_Normal(493, "Small Mauler", 10063, TitleType.NORMAL), 
        Smallmaul_Minor(494, "Banger", 10063, TitleType.MINOR), 
        Smallmaul_Master(495, "Masher", 10063, TitleType.MASTER), 
        Smallmetalshield_Normal(496, "Sturdy Light Defender", 10022, TitleType.NORMAL), 
        Smallmetalshield_Minor(497, "Sturdy Light Bastion", 10022, TitleType.MINOR), 
        Smallmetalshield_Master(498, "Sturdy Light Bulwark", 10022, TitleType.MASTER), 
        Smallwoodenshield_Normal(499, "Nimble Light Defender", 10019, TitleType.NORMAL), 
        Smallwoodenshield_Minor(500, "Nimble Light Bastion", 10019, TitleType.MINOR), 
        Smallwoodenshield_Master(501, "Nimble Light Bulwark", 10019, TitleType.MASTER), 
        Staff_Normal(502, "Disciple", 10090, TitleType.NORMAL), 
        Staff_Minor(503, "Monk", 10090, TitleType.MINOR), 
        Staff_Master(504, "Sensei", 10090, TitleType.MASTER), 
        StoneChisel_Normal(505, "Brickmaker", 10030, TitleType.NORMAL), 
        StoneChisel_Minor(506, "Chipper", 10030, TitleType.MINOR), 
        StoneChisel_Master(507, "One More Brick", 10030, TitleType.MASTER), 
        Twohandedsword_Normal(508, "Twohanded Swordsman", 10028, TitleType.NORMAL), 
        Twohandedsword_Minor(509, "Templar", 10028, TitleType.MINOR), 
        Twohandedsword_Master(510, "Paladin", 10028, TitleType.MASTER), 
        Warhammer_Normal(511, "Sledgehammer", 10070, TitleType.NORMAL), 
        Warhammer_Minor(512, "Dwarf", 10070, TitleType.MINOR), 
        Warhammer_Master(513, "Stag King", 10070, TitleType.MASTER), 
        Weaponlessfighting_Normal(514, "Lightweight", 10052, TitleType.NORMAL), 
        Weaponlessfighting_Minor(515, "Middleweight", 10052, TitleType.MINOR), 
        Weaponlessfighting_Master(516, "Heavyweight", 10052, TitleType.MASTER), 
        Shovel_Legendary(517, "Ace Of Spades", 10002, TitleType.LEGENDARY), 
        Shipbuilding_Legendary(518, "Ancient Mariner", 10082, TitleType.LEGENDARY), 
        Journal_T0(519, "Apprentice"), 
        Journal_T1(520, "Learned"), 
        Journal_T2(521, "Experienced"), 
        Journal_T3(522, "Skilled"), 
        Journal_T4(523, "Accomplished"), 
        Journal_T5(524, "Proficient"), 
        Journal_T6(525, "Talented"), 
        Restoration_Legendary(526, "Ancient Fraggle", 10095, TitleType.LEGENDARY), 
        Forester_Legendary(527, "Lorax", 10048, TitleType.LEGENDARY), 
        Journal_T7(528, "Professional"), 
        Journal_T8(529, "Expert"), 
        Journal_T9(530, "Master"), 
        Journal_P1(531, "Blessed"), 
        Journal_P2(532, "Angelic"), 
        Journal_P3(533, "Divine"), 
        Pickaxe_Legendary(534, "Mountain Eater", 10009, TitleType.LEGENDARY);
        
        public final int id;
        private final String name;
        private final String femaleName;
        final int skillId;
        final TitleType type;
        private static final Title[] titleArray;
        
        private Title(final int _id, final String _name) {
            this(_id, _name, TitleType.NORMAL);
        }
        
        private Title(final int _id, final String _name, final String _femaleName) {
            this(_id, _name, _femaleName, TitleType.NORMAL);
        }
        
        private Title(final int _id, final String _name, final TitleType _type) {
            this(_id, _name, -1, _type);
        }
        
        private Title(final int _id, final String _name, final String _femaleName, final TitleType _type) {
            this(_id, _name, _femaleName, -1, _type);
        }
        
        private Title(final int _id, final String _name, final int _skillId, final TitleType _type) {
            this(_id, _name, _name, _skillId, _type);
        }
        
        private Title(final int _id, final String _name, final String _femaleName, final int _skillId, final TitleType _type) {
            this.name = _name;
            this.femaleName = _femaleName;
            this.id = _id;
            this.skillId = _skillId;
            this.type = _type;
        }
        
        public int getTitleId() {
            return this.id;
        }
        
        public String getName() {
            return this.name;
        }
        
        public String getFemaleName() {
            return this.femaleName;
        }
        
        public String getName(final boolean getMaleName) {
            if (!getMaleName) {
                return this.femaleName;
            }
            return this.name;
        }
        
        public int getSkillId() {
            return this.skillId;
        }
        
        public TitleType getTitleType() {
            return this.type;
        }
        
        public boolean isRoyalTitle() {
            return this.id == Title.Kingdomtitle.id;
        }
        
        public static Title getTitle(final String titleName, final boolean isMaleName) {
            for (int x = 0; x < Title.titleArray.length; ++x) {
                if (Title.titleArray[x].getName(isMaleName).equalsIgnoreCase(titleName)) {
                    return Title.titleArray[x];
                }
            }
            return null;
        }
        
        public static Title getTitle(final int titleAsInt) {
            for (int i = 0; i < Title.titleArray.length; ++i) {
                if (titleAsInt == Title.titleArray[i].getTitleId()) {
                    return Title.titleArray[i];
                }
            }
            return null;
        }
        
        public static Title getTitle(final int skillId, final TitleType titleType) {
            for (int i = 0; i < Title.titleArray.length; ++i) {
                if (skillId == Title.titleArray[i].getSkillId() && titleType == Title.titleArray[i].getTitleType()) {
                    return Title.titleArray[i];
                }
            }
            return null;
        }
        
        static {
            titleArray = values();
        }
    }
}
