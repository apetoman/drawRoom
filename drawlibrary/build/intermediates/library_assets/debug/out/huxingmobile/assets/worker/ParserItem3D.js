
CHUNK_MESH_ID = 1; // 16-bit
CHUNK_MESH_VERTEX_ID = 11;
CHUNK_MESH_UV_ID = 12;
CHUNK_MESH_FACE_ID = 13;
CHUNK_MESH_FACE_SUB_ID = 131; // surface
CHUNK_MESH_MATERIAL_NAME = 14;
CHUNK_MESH_NORMAL_ID = 15; // normal
CHUNK_MTL_ID = 2;
CHUNK_MTL_DIFFUSE_COLOR_ID = 21;
CHUNK_MTL_DIFFUSE_MAP_ID = 22;
CHUNK_MTL_OPACITY_ID = 23;
CHUNK_MTL_NAME = 24;
CHUNK_MTL_DIFFUSE_MAP_UV_TRANSFORM = 25;
CHUNK_MTL_DIFFUSE_MAP_UV_SCALE = 251;
CHUNK_MTL_DIFFUSE_MAP_UV_ROTATE = 252;
CHUNK_MTL_DIFFUSE_MAP_UV_OFFSET = 253;


class ParserItem3D
{

    /**
     * 构造函数
     * @param data 
     */
    constructor(data)
    {
        this.objects = [];   // 生成的顶点数据
        this.currentObject = null;

        this.mtls = [];  // 生成的材质
        this.currentMtl = null;

        this.data = data;//2进制数据

        // 开始解析
        this.parse(data.position, data.bytesAvailable);
        // 合并材质相同的模型(可以大幅度优化性能)
        this.merge();
    }
    
    /**
     * 读取贴图url
     * @return 
     * 
     */		
    getMapUrls()
    {
        var ret = [];
        for(var mtl of this.mtls)
        {
            if (mtl.diffuseMapUrl != "")
            {
                ret.push(mtl.diffuseMapUrl);
            }
        }
        return ret;
    }
    
    
    
    readChunkInfo(dataPosition)
    {
        this.data.position = dataPosition;
        var chunkInfo = new ChunkInfo();
        chunkInfo.id = this.data.readUnsignedShort();
        chunkInfo.size = this.data.readUnsignedInt();
        chunkInfo.dataSize = chunkInfo.size - 6;
        chunkInfo.dataPosition = this.data.position;
        chunkInfo.nextChunkPosition = dataPosition + chunkInfo.size;
        return chunkInfo;
    }




    /**
     * 合并材质相同的模型(可以大幅度优化性能)
     */
    merge()
    {
        // 先以材质名为key创建字典归类
        var dict = {};
        var vo;
        for(vo of this.objects)
        {
            if (!dict[vo.materialName])
                dict[vo.materialName] = [];
            dict[vo.materialName].push(vo);
        }
        
        // 清空模型数组
        this.objects.length = 0;

        // 将相同材质的模型合并
        var otherObjects;
        for(var key in dict)
        {
            otherObjects = dict[key];//被合并对象
            var target = otherObjects[0];//合并对象

            if(otherObjects.length > 1)
            {
                var iLen = otherObjects.length;
                for(var i=1;i<iLen;i++)
                {
                    var numVertices = target.numVertices;
                    // 单个模型顶点数不能超过65535(2^16)
                    if(numVertices + otherObjects[i].numVertices > 65530)
                    {
                        this.objects.push(target);
                        target = otherObjects[i];
                        continue;
                    }

                    // GMath.mergeTwoArray(target.vertices, vos[i].vertices, true);
                    // GMath.mergeTwoArray(target.uvs, vos[i].uvs, true);
                    // GMath.mergeTwoArray(target.normals, vos[i].normals, true);

                    // 合并顶点
                    target.vertices = target.vertices.concat(otherObjects[i].vertices);
                    // 合并uv
                    target.uvs = target.uvs.concat(otherObjects[i].uvs);
                    // 合并法线
                    // target.normals = target.normals.concat(otherObjects[i].normals);
                    // 合并面索引
                    for(var index of otherObjects[i].indices)
                    {
                        target.indices.push(index + numVertices);
                    }
                }
            }
            this.objects.push(target);
        }
        dict = null;
    }

    


    /**
     * 解析模型2进制数据
     * @param dataPosition 起始位置
     * @param bytesAvailable 剩余字节数
     */
    parse(dataPosition, bytesAvailable)
    {
        // if (bytesAvailable < 6) return;
        // var chunkInfo = this.readChunkInfo(dataPosition);
        // switch (chunkInfo.id)
        // {
        //     case CHUNK_MESH_ID:
        //         this.currentObject = new ObjectVO();
        //         this.objects.push(this.currentObject);
        //         this.parseMesh(chunkInfo.dataPosition, chunkInfo.dataSize);
        //         break;
            
        //     case CHUNK_MTL_ID:
        //         this.currentMtl = new MaterialVO();
        //         this.mtls.push(this.currentMtl);
        //         this.parseMaterial(chunkInfo.dataPosition, chunkInfo.dataSize);
        //         break;
            
        //     default:
        //         console.debug("未识别的chunk id：" + chunkInfo.id);
        //         break;
        // }
        // this.parse(chunkInfo.nextChunkPosition, bytesAvailable - chunkInfo.size);

        while(bytesAvailable >= 6)
        {
            var chunkInfo = this.readChunkInfo(dataPosition);
            switch(chunkInfo.id)
            {
                case CHUNK_MESH_ID:
                    this.currentObject = new ObjectVO();
                    this.objects.push(this.currentObject);
                    this.parseMesh(chunkInfo.dataPosition, chunkInfo.dataSize);
                    break;
                
                case CHUNK_MTL_ID:
                    this.currentMtl = new MaterialVO();
                    this.mtls.push(this.currentMtl);
                    this.parseMaterial(chunkInfo.dataPosition, chunkInfo.dataSize);
                    break;
                
                default:
                    console.debug("未识别的chunk id：" + chunkInfo.id);
                    break;
            }

            // 起始位置和剩余字节数
            dataPosition = chunkInfo.nextChunkPosition;
            bytesAvailable -= chunkInfo.size;
        }
    }
    
    parseMesh(dataPosition, bytesAvailable)
    {
        while (bytesAvailable >= 6)
        {
            var chunkInfo = this.readChunkInfo(dataPosition);
            switch (chunkInfo.id)
            {
                case CHUNK_MESH_VERTEX_ID:
                    this.readVertices(chunkInfo.dataSize);
                    break;
                
                case CHUNK_MESH_UV_ID:
                    this.readUvs(chunkInfo.dataSize);
                    break;
                
                case CHUNK_MESH_FACE_ID:
                    this.parseFace(chunkInfo.dataSize);
                    break;
                
                case CHUNK_MESH_MATERIAL_NAME:
                    this.currentObject.materialName = this.readName(chunkInfo.dataSize);
                    break;

                case CHUNK_MESH_NORMAL_ID:
                    // this.readNormals(chunkInfo.dataSize);
                    break;
                
                default:
                    console.debug("未识别的chunk id：" + chunkInfo.id);
                    break;
            }
            dataPosition = chunkInfo.nextChunkPosition;
            bytesAvailable -= chunkInfo.size;
        }
    }
    
    parseFace(dataSize)
    {
        for (var i = 0; i < dataSize / 2; i ++)
            this.currentObject.indices.push(this.data.readUnsignedShort());
    }
    
    parseMaterial(dataPosition, bytesAvailable)
    {
//			trace("parseMaterial");
        while (bytesAvailable >= 6)
        {
            var chunkInfo = this.readChunkInfo(dataPosition);
            switch (chunkInfo.id)
            {
                case CHUNK_MTL_DIFFUSE_COLOR_ID:
                    this.currentMtl.diffuseColor = this.data.readUnsignedInt();
//						trace("    漫反射：0x" + this.currentMtl.diffuseColor.toString(16));
                    break;
                
                case CHUNK_MTL_DIFFUSE_MAP_ID:
                    var mapUrl = "";
                    for (var i = 0; i < chunkInfo.dataSize; i ++)
                        mapUrl += String.fromCharCode(this.data.readUnsignedByte());
                    mapUrl = mapUrl.replace("\\", "\/");
                    var arr = mapUrl.split(".");
                    if (arr.length > 1)
                    {
                        arr[arr.length - 1] = String(arr[arr.length - 1]).toLowerCase();
                        mapUrl = arr.join(".");
                    }
                    this.currentMtl.diffuseMapUrl = mapUrl;
                    break;
                
                case CHUNK_MTL_OPACITY_ID:
                    this.currentMtl.opacity = this.data.readFloat();
                    break;
                
                case CHUNK_MTL_NAME:
                    this.currentMtl.name = this.readName(chunkInfo.dataSize);
                    break;
                
                case CHUNK_MTL_DIFFUSE_MAP_UV_TRANSFORM:
                    break;
                
                default:
                    console.debug("未识别的chunk id：" + chunkInfo.id);
                    break;
            }
            dataPosition = chunkInfo.nextChunkPosition;
            bytesAvailable -= chunkInfo.size;
        }
    }


    
		
    
    
    readVertices(dataSize)
    {
        for (var i = 0; i < dataSize / 4; i ++)
        {
            this.currentObject.vertices.push(this.data.readFloat());
        }
    }
    
    readName(dataSize)
    {
        var ret = "";
        for (var i = 0; i < dataSize; i ++)
            ret += String.fromCharCode(this.data.readUnsignedByte());
        return ret;
    }
    
    readUvs(dataSize)
    {
        for (var i = 0; i < dataSize / 8; i ++)
        {
            this.currentObject.uvs.push(this.data.readFloat());
            this.currentObject.uvs.push(1 - this.data.readFloat());
        }
    }

    readNormals(dataSize)
    {
        for (var i = 0; i < dataSize / 4; i ++)
        {
            this.currentObject.normals.push(this.data.readFloat());
        }
    }
    
    getMtlByName(name)
    {
        for(var mtl of this.mtls)
        {
            if (mtl.name == name)
                return mtl;
        }
        return null;
    }
    
    
}





/**
 * 块数据
 */
class ChunkInfo 
{
    constructor()
    {
        this.id = 0;
        this.size = 0;
        this.dataSize = 0;
        this.dataPosition = 0;
        this.nextChunkPosition = 0;
    }
}




/**
 * 材质
 */
class MaterialVO
{
    constructor()
    {
        this.name = "";
        this.diffuseColor = 0;
        this.diffuseMapUrl = "";
        this.diffuseATFMapUrl = "";
        this.opacity = 1.0;
        this.texture = null;
    }
}




/**
 * 顶点数据
 */
class ObjectVO
{
    constructor()
    {
        this.vertices = [];
        this.uvs = [];
        this.indices = [];
        this.materialName = "";
    }

	
	isEmpty()
	{
		return this.vertices.length == 0 || this.indices.length == 0;
	}
	
	get numVertices()
	{
		return this.vertices.length / 3;
	}
	
	findUnusedVertex()
	{
		var used = [];
		var index;
		for(index of this.indices)
		{
			used[index] = true;
		}
		for (var i = 0; i < used.length; i ++)
			if (!used[i])
				console.debug("        未使用点：" + i);
	}
}




