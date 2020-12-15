package service;

/**
 * Created by CPU60126_LOCAL on 2020-07-09.
 */
public class HandlerError {
    public enum Error{
        SUCCESS((short)0),
        ERROR((short)1),
        PLAYERINFO_NULL((short)2),
        MAPINFO_NULL((short)3),
        EXCEPTION((short)4),;


        private final short value;
        private Error(short value){
            this.value = value;
        }

        public short getValue(){
            return this.value;
        }
    }
}
