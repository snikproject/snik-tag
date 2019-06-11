import docx4js from "docx4js"

docx4js.load("../benchmark/input.docx").then(docx=>
{
	const doc = docx.getObjectPart("word/document.xml");
//	console.log(doc);
//	console.log(docx.officeDocument.content("w\\:b"));
	const content = docx.officeDocument.content("w\\:b");
	for(let i=0; i<content.length; i++)
	{
		console.log(Object.getOwnPropertyNames(content[i].));
		break;
	}
	//docx.save("output.docx")

	//or use a event handler for more flexible control
//	const ModelHandler=require("docx4js/openxml/docx/model-handler").default;
	/*
	class MyModelhandler extends ModelHandler{
		onp({type,children,node,...}, node, officeDocument){

		}
	}
	let handler=new MyModelhandler()
	handler.on("*",function({type,children,node,...}, node, officeDocument){
		console.log("found model:"+type)
	})
	handler.on("r",function({type,children,node,...}, node, officeDocument){
		console.log("found a run")
	})

	docx.parse(handler)

	//you can change content on docx.officeDocument.content, and then save
	docx.officeDocument.content("w\\:t").text("hello")
	docx.save("/output.docx")
	*/

});
