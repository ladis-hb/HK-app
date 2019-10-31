package lads.dev.biz;

public interface RuleEnum {
    enum RuleStep1 {

        ASCII("ascii"),NUMBER("number"),MIX("mix");

        private String name;
        private RuleStep1(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    enum RuleStep3 {

        LEN("len"),SPLIT("split");

        private String name;
        private RuleStep3(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    enum RuleStep3SplitSymbol {
        WHITESPACE("whitespace"," ");

        private String name;
        private String value;
        private RuleStep3SplitSymbol(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    enum WarnType {

        NO_WARN("0"), UPPER_LOWER_LIMITS("1"), WARN_CONFIG("2");

        private String type;
        private WarnType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return type.toString();
        }
    }

    enum ParseFlag {

        NO_PARSE("0"), PARSE("1");

        private String s;
        private ParseFlag(String s) {
            this.s = s;
        }

        @Override
        public String toString() {
            return s.toString();
        }
    }

    enum DataType2{
        INT("int"),SHORT("short"),USHORT("ushort"),FLOAT("float");

        private String s;
        private DataType2(String s) {
            this.s = s;
        }

        @Override
        public String toString() {
            return s.toString();
        }
    }

    enum ReadType {
        MODBUS("1"),LADSUPS("2");
        private String s;
        private ReadType(String s) {
            this.s = s;
        }

        @Override
        public String toString() {
            return s.toString();
        }
    }

    enum OnOffFlag {
        ON("1"), OFF("0");
        private String s;
        private OnOffFlag(String s) {
            this.s = s;
        }

        @Override
        public String toString() {
            return s.toString();
        }
    }

    enum OptType {
        OPERATE("operate"), QUERY("query");
        private String s;
        private OptType(String s) {
            this.s = s;
        }

        @Override
        public String toString() {
            return s.toString();
        }
    }
}
