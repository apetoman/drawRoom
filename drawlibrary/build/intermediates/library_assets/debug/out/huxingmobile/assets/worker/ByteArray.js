/**
 * 2进制数组
 */
class ByteArray
{
    constructor(bytes = null)
    {
        this.bytes = null;
        this._position = 0;

        // 大端小端
        this.endian = true;//默认小端

        if(bytes)
        {
            this.bytes = new DataView(bytes , 0 , bytes.byteLength);
        }
        else
        {
            let ab = new ArrayBuffer(100000);
            this.bytes = new DataView(ab , 0 , ab.byteLength);
        }
    }



    set position(value)
    {
        this._position = value;
        this._position = Math.max(0 , this._position);
        this._position = Math.min(this.bytes.byteLength , this._position);
    }



    get position()
    {
        return this._position;
    }




    readBoolean()
    {
        let offset = this.position + 1;
        if(offset <= this.bytes.byteLength)
        {
            let v = this.bytes.getUint8(this.position);
            this.position += 1;
            return v == 1;
        }
        return false;
    }




    readByte()
    {
        let offset = this.position + 1;
        if(offset <= this.bytes.byteLength)
        {
            let v = this.bytes.getInt8(this.position);
            this.position += 1;
            return v;
        }
        return 0;
    }




    readDouble()
    {
        let offset = this.position + 8;
        if(offset <= this.bytes.byteLength)
        {
            let v = this.bytes.getFloat64(this.position , this.endian);
            this.position += 8;
            return v;
        }
        return 0;
    }



    readFloat()
    {
        let offset = this.position + 4;
        if(offset <= this.bytes.byteLength)
        {
            let v = this.bytes.getFloat32(this.position , this.endian);
            this.position += 4;
            return v;
        }
        return 0;
    }



    readInt()
    {
        let offset = this.position + 4;
        if(offset <= this.bytes.byteLength)
        {
            let v = this.bytes.getInt32(this.position , this.endian);
            this.position += 4;
            return v;
        }
        return 0;
    }




    readShort()
    {
        let offset = this.position + 2;
        if(offset <= this.bytes.byteLength)
        {
            let v = this.bytes.getInt16(this.position , this.endian);
            this.position += 2;
            return v;
        }
        return 0;
    }



    readUnsignedByte()
    {
        let offset = this.position + 1;
        if(offset <= this.bytes.byteLength)
        {
            let v = this.bytes.getUint8(this.position);
            this.position += 1;
            return v;
        }
        return 0;
    }




    readUnsignedInt()
    {
        let offset = this.position + 4;
        if(offset <= this.bytes.byteLength)
        {
            let v = this.bytes.getUint32(this.position , this.endian);
            this.position += 4;
            return v;
        }
        return 0;
    }




    readUnsignedShort()
    {
        let offset = this.position + 2;
        if(offset <= this.bytes.byteLength)
        {
            let v = this.bytes.getUint16(this.position , this.endian);
            this.position += 2;
            return v;
        }
        return 0;
    }




    /**
     * 读取指定长度的UTF8字符串
     * @param length 
     */
    readUTFBytes(length)
    {
        let offset = this.position + length * 2;
        if(offset <= this.bytes.byteLength)
        {
            let str = "";
            let iLen = length * 2;
            for(var i=0;i<iLen;i+=2)
            {
                var char = this.bytes[this.position + i];
                str += String.fromCharCode(char);
            }
            this.position += length * 2;
            return str;
        }
        return "";
    }




    writeByte(value)
    {
        let offset = this.position + 1;
        if(offset <= this.bytes.byteLength)
        {
            this.bytes.setInt8(this.position , value);
            this.position += 1;
        }
    }




    writeBoolean(value)
    {
        let offset = this.position + 1;
        if(offset <= this.bytes.byteLength)
        {
            this.bytes.setUint8(this.position , value == true ? 1 : 0);
            this.position += 1;
        }
    }




    writeDouble(value)
    {
        let offset = this.position + 8;
        if(offset <= this.bytes.byteLength)
        {
            this.bytes.setFloat64(this.position , value , this.endian);
            this.position += 8;
        }
    }





    writeFloat(value)
    {
        let offset = this.position + 4;
        if(offset <= this.bytes.byteLength)
        {
            this.bytes.setFloat32(this.position , value , this.endian);
            this.position += 4;
        }
    }





    writeInt(value)
    {
        let offset = this.position + 4;
        if(offset <= this.bytes.byteLength)
        {
            this.bytes.setInt32(this.position , value , this.endian);
            this.position += 4;
        }
    }





    writeShort(value)
    {
        let offset = this.position + 2;
        if(offset <= this.bytes.byteLength)
        {
            this.bytes.setInt16(this.position , value , this.endian);
            this.position += 2;
        }
    }




    writeUnsignedInt(value)
    {
        let offset = this.position + 4;
        if(offset <= this.bytes.byteLength)
        {
            this.bytes.setUint32(this.position , value , this.endian);
            this.position += 4;
        }
    }




    /**
     * 写入UTF8字符串
     * @param value 
     */
    writeUTF(value)
    {
        let offset = this.position + (value.length * 2);
        if(offset <= this.bytes.byteLength)
        {
            let iLen = value.length;
            for(var i=0;i<iLen;i++)
            {
                var char = value.charCodeAt(i);
                this.bytes[this.position] = char;
                this.position += 2;
            }
        }
    }




    get bytesAvailable()
    {
        return this.bytes.byteLength - this.position;
    }




    clear()
    {
        let ab = new ArrayBuffer(100000);
        this.bytes = new DataView(ab , 0 , ab.byteLength);

        this.position = 0;
    }
}
