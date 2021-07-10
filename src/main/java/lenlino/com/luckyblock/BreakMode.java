package lenlino.com.luckyblock;

public enum BreakMode {
    ONE,TREE,FIVE;

    @Override
    public String toString() {
        if(this==ONE){
            return "1*1*1Mode";
        }else if(this==FIVE){
            return "5*5*5Mode";
        }else{
            return "3*3*3Mode";
        }
    }
}
