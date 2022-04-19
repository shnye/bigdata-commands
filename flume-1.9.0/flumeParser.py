import os
import json

class FlumeParser:
    def __init__(self,agent,configFile,defaultFile,flumeFile):
        self.agent=agent
        self.configFile = configFile
        self.defaultFile = defaultFile
        self.flumeFile = flumeFile
        self.componts={"sources":{"netcat":"rNetcat","netcatudp":"rNetcatudp","http":"rHTTP",
                                  "org.apache.flume.source.kafka.KafkaSource":"rKafka","TAILDIR":"rTaildir",
                                  "avro":"rAvro","multiports_syslogtcp":"rMultiportsSyslogtcp"},
                        "sinks":{"logger":"kLogger","avro":"kAvro","hdfs":"kHDFS","file_roll":"kFileRoll",
                                 "org.apache.flume.sink.elasticsearch.ElasticSearchSink":"kES","http":"kHTTP",
                                "org.apache.flume.sink.kafka.KafkaSink":"kKafka"},
                        "channels":{"memory":"cMemory","file":"cFile",
                                    "org.apache.flume.channel.kafka.KafkaChannel":"cKafka"},
                        "interceptors":{"timestamp":"iTimestamp","static":"iStatic",
                                        "search_replace":"iSearchReplace","regex_extractor":"iRegexExtractor"},
                        "selector":{"multiplexing":"sMultiplexing","replicating":"sReplicating"},
                        "processor":{"load_balance":"pLoadBlance","failover":"pFailover"}
                      }
        self.check={"rNetcat":["type","bind","port"],
                         "rNetcatudp":["type","bind","port"],
                         "rHTTP":["type","port"],
                         "rKafka":["type","kafka.bootstrap.servers",["kafka.topics","kafka.topics.regex"]],
                         "rAvro":["type","bind","port"],
                         "rMultiportsSyslogtcp":["type","host","ports"],
                         "rTaildir":["type","filegroups","filegroups.f1"],
                         "kLogger":["type"],
                         "kAvro":["type","hostname","port"],
                         "kHDFS":["type","hdfs.path"],
                         "kFileRoll":["type","sink.directory"],
                         "kES":["type","hostNames"],
                         "kHTTP":["type","endpoint"],
                         "kKafka":["type","kafka.bootstrap.servers"],
                         "cMemory":["type"],
                         "cFile":["type"],
                         "cKafka":["type","kafka.bootstrap.servers"],
                         "iTimestamp":["type"],
                         "iStatic":["type"],
                         "iSearchReplace":["type"],
                         "iRegexExtractor":["type","regex","serializers"],
                         "sReplicating":["type"],
                         "sMultiplexing":["type"],
                         "pFailover":["type"],
                         "pLoadBlance":["type"]
                   }
        self.must=["sources","sinks","channels"]
        self.sources={"id":"sources","content":None,"flag":"r","items":[]}
        self.channels={"id":"channels","content":None,"flag":"c","items":[]}
        self.sinks={"id":"sinks","content":None,"flag":"k","items":[]}
        self.interceptors={"id":"interceptors","content":None,"flag":"i","items":[]}
        self.selector={"id":"selector","content":None,"flag":"s","items":[]}
        self.processor={"id":"processor","content":None,"flag":"g","items":[]}


        with open(defaultFile) as f:
            self.default=json.loads(f.read())
        #with open(configFile) as f:
        #    self.config=json.loads(f.read())
        self.config = json.loads(os.getenv("CONFIG"))
        self.preCombine(self.default)
        self.preCombine(self.config)
        self.combineConfig()


        self.flumeText=""
        self.setKeyArr("sinks")
        self.setKeyArr("sources")
        self.setKeyArr("channels")
        self.setKeyArr("interceptors")
        self.setKeyArr("selector")
        self.setKeyArr("processor")
    def preCombine(self,conf):
        for key in self.componts.keys():
            value = conf.get(key)
            if type(value)==list:
                for idx,item in enumerate(value):
                    if type(item)==str:
                        value[idx] = {item:self.default.get(item)}
                    elif type(item)==dict and item.get("type"):
                        value[idx] = {self.componts[key][item.get("type")]:item}
            elif type(value)==str:
                value = {value:self.default.get(value)}
            elif type(value)==dict and value.get("type"):
                value = {self.componts[key][value.get("type")]:value}
            conf[key]=value
 
    def checkMustin(self,key):
        if self.config[key]:
            tobeChecks = self.config[key]
            if type(tobeChecks)!=list: tobeChecks=[tobeChecks]
            for tobeCheck in tobeChecks:
                key2 = self.componts[key][tobeCheck["type"]]
                mustbe = self.check[key2]
                for item in mustbe:
                    if type(item)==list:
                        ok=False
                        for item1 in item:
                            if item1 in list(tobeCheck.keys()):
                                ok=True
                        if not ok:
                            raise Exception("'%s' must be set in %s" % (item,{self.config[key]}))
                    else:
                        if item not in list(tobeCheck.keys()):
                            raise Exception("'%s' must be set in %s" % (item,{self.config[key]}))


    def combineConfig(self):
        #use self.config to combine self.default
        for key in self.componts.keys():
            value_config = self.config[key]
            value_default = self.default[key]
            #print(key,value_config,value_default)
            if value_config :
                value_default=self.default.get(key)
                if type(value_config)==list:
                    for idx,item in enumerate(value_config):
                        key1=list(item.keys())[0]
                        value_config[idx]={**self.default.get(key1),**value_config[idx][key1]}
                    self.config[key]=value_config
                elif type(value_config)==dict:   
                    key1=list(value_config.keys())[0]
                    value_config[key1]={**self.default[key1],**value_config[key1]}
                    self.config[key]=value_config[key1]
            elif key in self.must:
                if type(value_default)==list:
                    self.config[key]=list(map(lambda item:list(item.values())[0],value_default))
                else:
                    self.config[key]=list(value_default.values())[0]
            #print("==>",self.config[key])
            self.checkMustin(key)
    def getComponet(self,id):
        if id=="sources":
            return self.sources
        elif id=="sinks":
            return self.sinks
        elif id=="channels":
            return self.channels
        elif id=="interceptors":
            return self.interceptors
        elif id=="selector":
            return self.selector
        elif id=="processor":
            return self.processor
    def addLine(self,key=None,value=None):
        if key and value:
            self.flumeText += key +" = "+value+"\n"
        elif key:
            self.flumeText += key +"\n"
        else:
            self.flumeText += "\n"
        return self.flumeText
    def toString(self,arr):
        lst = ""
        for item in arr:
            lst += item+" " 
        return lst.rstrip()
    def setKeyArr(self,id):
        arr=[]
        componet = self.getComponet(id)
        if type(self.config.get(id))==list:
            arr=self.config.get(id)
        elif self.config.get(id)!=None:
            arr.append(self.config.get(id))
        componet["content"]=arr
        for idx in range(len(arr)):
            componet["items"].append(componet["flag"]+str(idx))
        return arr
    def genHeader(self,id):
        componet = self.getComponet(id)
        lst = self.toString(componet["items"])
        self.addLine(self.agent+"."+componet["id"] , lst )
    def genComponet(self,id):
        componet=self.getComponet(id)
        if len(componet["content"])==0: return
        self.addLine("#assemble "+id)
        if id=="processor":
            self.addLine(self.agent+".sinkgroups" , self.toString(componet["items"]))
            self.addLine(self.agent+".sinkgroups."+self.toString(componet["items"])+".sinks" ,  
                              self.toString(self.sinks["items"]))
        for index,item in enumerate(componet["content"]):
            if item==None: break
            item_type = item.get("type")
            if item_type:
                flagNum=componet["flag"]+str(index)
                for k,v in item.items():
                    if id=="interceptors":
                        self.addLine(self.agent+"."+"sources."+self.toString(self.sources["items"])+"."+componet["id"]+"."+flagNum+"."+k , str(v))
                    elif id=="selector":
                        if k=="mapping":
                            for idx,mp in enumerate(v):
                                self.addLine(self.agent+"."+"sources."+self.toString(self.sources["items"])+"."+componet["id"]+"."+k+"."+str(mp) , self.channels["items"][idx])
                        else:
                            self.addLine(self.agent+"."+"sources."+self.toString(self.sources["items"])+"."+componet["id"]+"."+k , str(v))
                    elif id=="processor":
                        self.addLine(self.agent+".sinkgroups."+self.toString(componet["items"])+"."+componet["id"]+"."+k , str(v))
                        if k=="type" and str(v)=="failover": 
                            for j,itemJ in enumerate(self.sinks["items"]):
                                self.addLine(self.agent+".sinkgroups."+self.toString(componet["items"])+"."+componet["id"]+".priority."+itemJ ,str((j+1)*100) )
                    else:
                        self.addLine(self.agent+"."+componet["id"]+"."+flagNum+"."+k , str(v))
            else:
                break
            if id=="sinks":
                self.addLine(self.agent+"."+componet["id"]+"."+flagNum+".channel" , "c"+str(index))
            self.addLine()
        if id=="sources":
            self.addLine(self.agent+"."+componet["id"]+"."+componet["items"][0]+".channels" , self.toString(self.channels["items"]))
        if id=="interceptors":
            self.addLine(self.agent+"."+"sources."+self.toString(self.sources["items"])+".interceptors" , self.toString(self.interceptors["items"]))    
        self.addLine()
    def genFlumeConfig(self):
        self.addLine("#assemble base")
        self.genHeader("sources")
        self.genHeader("channels")
        self.genHeader("sinks")
        self.addLine()
        self.genComponet("sources")
        self.genComponet("channels")
        self.genComponet("sinks")
        self.genComponet("interceptors")
        self.genComponet("selector")
        self.genComponet("processor")


        print(flumeParser.flumeText)


        with open(self.flumeFile,"w") as f:
            f.write(self.flumeText)


if __name__ == '__main__':
    flumeParser = FlumeParser("agent","config.json","default.json","flume.conf")
    flumeParser.genFlumeConfig()
