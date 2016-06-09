package opinyon.com.bruno.opinyon.util;

/**
 * Created by Bruno on 07/06/2016.
 */
public enum EnumOpt {
    UNKNOW		    ("UNKNOW","?",	99),
    sim		        ("sim","Sim",	0),
    nao		        ("nao","Não",	1),
    impeachment		("impeachment","Você é a favor do impeachment?",	2),
    presidential	("presidential","Eleição Presidencial",	3),
    lula	        ("lula","Luiz Inácio Lula da Silva",	4),
    aecio	        ("aecio","Aécio Neves",	5),
    bolso	        ("bolso","Jair Bolssonaro",	6),
    marina	        ("marina","Marina",	7),
            ;

    private String realName;
    private String shortname;
    private int code;

    private EnumOpt( String realName, String shortname, int status ) {
        this.realName = realName;
        this.shortname = shortname;
        this.code = status;
    }

    public String getRealName() {
        return realName;
    }
    public String getShortname() {
        return shortname;
    }
    public int getCode() {
        return code;
    }

    public static EnumOpt getEnumDeactivationType(int type) {
        for (EnumOpt t : values()) {
            if (type == t.getCode()) return t;
        }
        return EnumOpt.UNKNOW;
    }
}
