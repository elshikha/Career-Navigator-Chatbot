import java.io._
import sys.process._
import java.util.Date
import java.text.SimpleDateFormat
import scala.io.Source
import scala.io.StdIn._
import scala.annotation.tailrec
import scala.annotation.meta.field
import scala.util.Random
import scala.annotation.retains
import java.io.{File, PrintWriter}
import scala.collection.View.Empty



var userSuggestedRandC: List[String] = List()


//different responses that the user will get before displaying the roles
val responseTemplates = List(
    "Based on your input, here are some careers and roles that might interest you:\n{roles}.",
    "I found some potential career paths for you:\n{roles}.",
    "Here are a few career options you might consider:\n{roles}.",
    "You could explore roles such as:\n{roles}.",
    "Considering your interests, you might enjoy these career paths:\n{roles}."
)

val startTemplates = List(
    "Certainly, a ",
    "Definitely, a ",
    "Of course, a ",
    "Here you go, a ",
    "Absolutely, a ",
)

val careersAndRolesMap = Map(
        // Prog Languages mappings
        "python" -> List("Python Developer", "Data Scientist", "Machine Learning Engineer", "Web Developer"),
        "py" -> List("Python Developer", "Data Scientist", "Machine Learning Engineer", "Web Developer"),
        "java" -> List("Java Developer", "Android Developer", "Software Engineer", "Backend Developer"),
        "javascript" -> List("JavaScript Developer", "Full-stack Developer", "Frontend Developer", "Node.js Developer"),
        "c++" -> List("C++ Developer", "Software Engineer", "Game Developer", "Systems Programmer"),
        "cpp" -> List("C++ Developer", "Software Engineer", "Game Developer", "Systems Programmer"),
        "c#" -> List(".NET Developer", "Software Engineer", "Backend Developer"),
        ".net" -> List(".NET Developer", "Software Engineer", "Web Developer"),
        "swift" -> List("iOS Developer", "Swift Developer", "Mobile App Developer"),
        "go" -> List("Go Developer", "Backend Developer", "Cloud Engineer"),
        "ruby" -> List("Ruby Developer", "Web Developer", "Backend Developer"),
        "php" -> List("PHP Developer", "Web Developer", "Backend Developer"),
        "rust" -> List("Rust Developer", "Systems Programmer", "Backend Developer"),
        "kotlin" -> List("Kotlin Developer", "Android Developer", "Mobile App Developer"),
        "typescript" -> List("TypeScript Developer", "Frontend Developer", "Full-stack Developer"),
        "perl" -> List("Perl Developer", "Scripting Engineer"),
        "scala" -> List("Scala Developer", "Backend Developer", "Big Data Engineer"),
        "lua" -> List("Lua Developer", "Game Developer", "Scripting Engineer"),
        "r" -> List("R Developer", "Data Analyst", "Data Scientist"),
        "haskell" -> List("Haskell Developer", "Functional Programmer", "Researcher"),
        "dart" -> List("Dart Developer", "Flutter Developer", "Mobile App Developer"),
        "matlab" -> List("MATLAB Developer", "Data Scientist", "Researcher"),
        "lisp" -> List("Lisp Developer", "AI Developer", "Researcher"),
        "sql" -> List("Database Administrator", "Data Analyst", "Data Engineer"),
        "html" -> List("Frontend Developer", "Web Developer"),
        "css" -> List("Frontend Developer", "Web Developer", "UI/UX Designer"),
        "bash" -> List("Scripting Engineer", "DevOps Engineer"),
        "c" -> List("C Developer", "Systems Programmer", "Embedded Systems Engineer"),
        "assembly language" -> List("Low-level Programmer", "Systems Programmer", "Embedded Systems Engineer"),
        "clojure" -> List("Clojure Developer", "Backend Developer", "Functional Programmer"),
        "f#" -> List("F# Developer", "Backend Developer", "Functional Programmer"),
        "julia" -> List("Julia Developer", "Data Scientist", "Researcher"),
        "erlang" -> List("Erlang Developer", "Backend Developer", "Concurrent Programming Specialist"),
        "groovy" -> List("Groovy Developer", "Scripting Engineer", "Backend Developer"),
        // Skills mappings
        "problem" -> List("Problem Solver","Software Engineer", "Data Scientist"),
        "solving" -> List("Problem Solver","Software Engineer", "Data Scientist"),
        "ps" -> List("Software Engineer", "Data Scientist"),
        "critical" -> List("Analyst", "Researcher"),
        "thinking" -> List("Analyst", "Researcher"),
        "communication" -> List("Communications Specialist", "Software Engineer", "Data Scientist"),
        "collaboration" -> List("Software Engineer", "Data Scientist"),
        "teamwork" -> List("Software Engineer", "Data Scientist"),
        "adaptability" -> List("Software Engineer", "Data Scientist"),
        "adaptation" -> List("Software Engineer", "Data Scientist"),
        "time" -> List("Project Manager", "Software Engineer"),
        "management" -> List("Project Manager", "Software Engineer"),
        "leadership" -> List("Team Lead", "Project Manager"),
        "attention" -> List("Quality Assurance Engineer", "Data Analyst"),
        "details" -> List("Quality Assurance Engineer", "Data Analyst"),
        "creativity" -> List("UI/UX Designer", "Game Developer"),
        "innovation" -> List("Researcher", "Product Developer"),
        "self-motivation" -> List("Freelancer"),
        "self-management" -> List("Freelancer"),
        "analytical" -> List("Analyst", "Data Analyst", "Researcher"),
        "technical" -> List("Technical Expert", "Software Engineer", "Data Scientist"),
        "soft" -> List("Manager", "Consultant"),
        "coding" -> List("Software Engineer", "Full-stack Developer", "Backend Developer"),
        "debugging" -> List("Debugging Specialist", "Software Engineer", "Quality Assurance Engineer"),
        "troubleshooting" -> List("Troubleshooting Expert", "Support Engineer", "IT Specialist"),
        "testing" -> List("Software Tester", "Quality Assurance Engineer", "Testing Specialist"),
        "version" -> List("Version Control Specialist", "Software Engineer", "Developer"),
        "control" -> List("Version Control Specialist", "Software Engineer", "Developer"),
        "git" -> List("Version Control Specialist", "Software Engineer", "Developer"),
        "agile" -> List("Agile Practitioner", "Scrum Master", "Software Engineer"),
        "methodologies" -> List("Agile Practitioner", "Scrum Master", "Software Engineer"),
        "project" -> List("Project Manager", "Project Coordinator", "Software Engineer"),
        "presentation" -> List( "Sales Engineer", "Trainer"),
        "public" -> List("Presenter", "Evangelist"),
        "speaking" -> List("Presenter", "Evangelist"),
        "writing" -> List("Technical Writer","Documentation Specialist"),
        "documentation" -> List("Technical Writer","Documentation Specialist"),
        "research" -> List("Researcher", "Academic", "Scientist"),
        "identification" -> List("Problem Identifier", "Analyst", "Quality Assurance Engineer"),
        "framing" -> List("Problem Framer", "Product Manager", "Business Analyst"),
        "solution" -> List("Software Engineer", "Data Scientist"),
        "design" -> List("Designer", "UI/UX Designer", "Product Designer"),
        "decision" -> List("Decision Maker", "Manager", "Director"),
        "making" -> List("Decision Maker", "Manager", "Director"),
        "learning" -> List("Researcher", "Developer"),
        "agility" -> List("Agile Practitioner", "Adaptable Professional", "Freelancer"),
        "emotional" -> List("Manager", "Consultant"),
        "intelligence" -> List("Manager", "Consultant"),
        "interpersonal" -> List("Manager", "HR Specialist"),
        "negotiation" -> List("Salesperson", "Business Developer"),
        "conflict" -> List("HR Specialist"),
        "resolution" -> List("HR Specialist"),
        "customer" -> List("Customer Service Representative", "Support Engineer", "Sales Engineer"),
        "service" -> List("Customer Service Representative", "Support Engineer", "Sales Engineer"),
        "user" -> List("User Experience Specialist", "UI/UX Designer", "Product Manager"),
        "experience" -> List("User Experience Specialist", "UI/UX Designer", "Product Manager"),
        // Topics mappings
        "data" -> List("Data Scientist", "Data Analyst", "Big Data Engineer"),
        "structures" -> List("Software Engineer", "Data Scientist", "Computer Scientist"),
        "ds" -> List("Software Engineer", "Data Scientist", "Computer Scientist"),
        "algorithms" -> List("Software Engineer", "Data Scientist", "Computer Scientist"),
        "algo" -> List("Software Engineer", "Data Scientist", "Computer Scientist"),
        "object-oriented" -> List("Software Engineer", "Java Developer", "C# Developer"),
        "programming" -> List("Software Engineer", "Full-stack Developer", "Backend Developer"),
        "oop" -> List("Software Engineer", "Full-stack Developer", "Backend Developer"),
        "functional" -> List("Functional Programmer", "Software Engineer", "Backend Developer"),
        "fp" -> List("Functional Programmer", "Software Engineer", "Backend Developer"),
        "dynamic" -> List("Software Engineer", "Full-stack Developer", "Backend Developer"),
        "dp" -> List("Software Engineer", "Full-stack Developer", "Backend Developer"),
        "recursion" -> List("Software Engineer", "Data Scientist", "Computer Scientist"),
        "rec" -> List("Software Engineer", "Data Scientist", "Computer Scientist"),
        "graph" -> List("Software Engineer", "Data Scientist", "Computer Scientist"),
        "theory" -> List("Computer Scientist", "Software Engineer", "Data Scientist"),
        "graphs" -> List("Computer Scientist", "Software Engineer", "Data Scientist"),
        "search" -> List("Software Engineer", "Data Scientist", "Computer Scientist"),
        "sorting" -> List("Software Engineer", "Data Scientist", "Computer Scientist"),
        "greedy" -> List("Software Engineer", "Data Scientist", "Computer Scientist"),
        "neural" -> List("Machine Learning Engineer", "Data Scientist", "AI Developer"),
        "networks" -> List("Network Engineer", "Data Scientist", "Computer Scientist"),
        "nns" -> List("Machine Learning Engineer", "Data Scientist", "AI Developer"),
        "visualization" -> List("Data Scientist", "Data Analyst", "UI/UX Designer"),
        "dv" -> List("Data Scientist", "Data Analyst", "UI/UX Designer"),
        "parallel" -> List("Parallel Computing Specialist", "Software Engineer", "Data Scientist"),
        "concurrent" -> List("Parallel Computing Specialist", "Software Engineer", "Data Scientist"),
        "parallelism" -> List("Parallel Computing Specialist", "Software Engineer", "Data Scientist"),
        "concurrency" -> List("Parallel Computing Specialist", "Software Engineer", "Data Scientist"),
        "protocols" -> List("Network Engineer", "Security Engineer", "Software Engineer"),
        "distributed" -> List("Distributed Systems Engineer", "Cloud Engineer", "Software Engineer"),
        // Fields mappings
        "web" -> List("Web Developer", "Full-stack Developer", "Frontend Developer"),
        "development" -> List("Software Engineer", "Full-stack Developer", "Backend Developer"),
        "webdev" -> List("Web Developer", "Full-stack Developer", "Frontend Developer"),
        "mobile" -> List("Mobile App Developer", "iOS Developer", "Android Developer"),
        "app" -> List("Mobile App Developer", "iOS Developer", "Android Developer"),
        "dev" -> List("Software Engineer", "Full-stack Developer", "Backend Developer"),
        "data" -> List("Data Scientist", "Data Analyst", "Data Engineer"),
        "science" -> List("Data Scientist", "Data Analyst", "Research Scientist"),
        "analysis" -> List("Data Scientist", "Data Analyst", "Research Scientist"),
        "machine" -> List("Machine Learning Engineer", "Data Scientist", "AI Developer"),
        "learning" -> List("Machine Learning Engineer", "Data Scientist", "AI Developer"),
        "ml" -> List("Machine Learning Engineer", "Data Scientist", "AI Developer"),
        "artificial" -> List("AI Developer", "Machine Learning Engineer", "Data Scientist"),
        "intelligence" -> List("AI Developer", "Machine Learning Engineer", "Data Scientist"),
        "ai" -> List("AI Developer", "Machine Learning Engineer", "Data Scientist"),
        "game" -> List("Game Developer", "Game Designer", "Software Engineer"),
        "gamedev" -> List("Game Developer", "Game Designer", "Software Engineer"),
        "networking" -> List("Network Engineer", "Security Engineer", "DevOps Engineer"),
        "networks" -> List("Network Engineer", "Security Engineer", "DevOps Engineer"),
        "cybersecurity" -> List("Cybersecurity Engineer", "Security Analyst", "Security Consultant"),
        "security" -> List("Cybersecurity Engineer", "Security Analyst", "Security Consultant"),
        "infosec" -> List("Cybersecurity Engineer", "Security Analyst", "Security Consultant"),
        "embedded" -> List("Embedded Systems Engineer", "Hardware Engineer", "Firmware Engineer"),
        "systems" -> List("Systems Engineer", "System Administrator", "DevOps Engineer"),
        "robotics" -> List("Robotics Engineer", "Automation Engineer", "Mechatronics Engineer"),
        "scientific" -> List("Scientist", "Research Scientist", "Data Scientist"),
        "computing" -> List("Software Engineer", "Computer Scientist", "Cloud Engineer"),
        "computation" -> List("Software Engineer", "Computer Scientist", "Cloud Engineer"),
        "cloud" -> List("Cloud Engineer", "DevOps Engineer", "System Administrator"),
        "natural" -> List("NLP Engineer", "Computational Linguist", "AI Developer", "Data Scientist", "Machine Learning Engineer"),
        "language" -> List("NLP Engineer", "Computational Linguist", "AI Developer", "Data Scientist", "Machine Learning Engineer"),
        "processing" -> List("NLP Engineer", "Computational Linguist", "AI Developer", "Data Scientist", "Machine Learning Engineer"),
        "nlp" -> List("NLP Engineer", "Computational Linguist", "AI Developer", "Data Scientist", "Machine Learning Engineer"),
        "databases" -> List("Database Administrator", "Data Engineer", "Software Engineer"),
        "database" -> List("Database Administrator", "Data Engineer", "Software Engineer"),
        "management" -> List("Project Manager", "Product Manager", "Team Lead"),
        "software" -> List("Software Engineer", "Software Developer", "Software Architect"),
        "engineering" -> List("Software Engineer", "Software Developer", "Software Architect"),
        "dev" -> List("Software Engineer", "Software Developer", "Software Architect"),
        "computer" -> List("Computer Scientist", "Software Engineer", "System Administrator"),
        "graphics" -> List("Graphics Programmer", "Game Developer", "UI/UX Designer"),
        "operating" -> List("System Administrator", "Systems Engineer", "DevOps Engineer"),
        "os" -> List("System Administrator", "Systems Engineer", "DevOps Engineer"),
        "blockchain" -> List("Blockchain Developer", "Blockchain Engineer", "Cryptocurrency Developer"),
        "blockchaining" -> List("Blockchain Developer", "Blockchain Engineer", "Cryptocurrency Developer"),
        "big" -> List("Big Data Engineer", "Data Scientist", "Data Analyst"),
        "iot" -> List("IoT Developer", "Embedded Systems Engineer", "Hardware Engineer"),
        "Internet" -> List("Internet Engineer", "Network Engineer", "Web Developer"),
        "of" -> List("Internet Engineer", "Network Engineer", "Web Developer"),
        "Things" -> List("IoT Developer", "Embedded Systems Engineer", "Hardware Engineer"),
        "bioinformatics" -> List("Bioinformatics Scientist", "Computational Biologist", "Bioinformatician"),
        "bioinfo" -> List("Bioinformatics Scientist", "Computational Biologist", "Bioinformatician"),
        "virtual" -> List("VR Developer", "AR Developer", "Game Developer"),
        "reality" -> List("VR Developer", "AR Developer", "Game Developer"),
        "vr" -> List("VR Developer", "AR Developer", "Game Developer"),
        "augmented" -> List("AR Developer", "VR Developer", "Game Developer"),
        "ar" -> List("AR Developer", "VR Developer", "Game Developer"),
        "computer" -> List("Computer Vision Engineer", "AI Developer", "Software Engineer"),
        "vision" -> List("Computer Vision Engineer", "AI Developer", "Software Engineer"),
        "cv" -> List("Computer Vision Engineer", "AI Developer", "Software Engineer"),
        "geographic" -> List("GIS Developer", "Geospatial Analyst", "Cartographer"),
        "information" -> List("GIS Developer", "Geospatial Analyst", "Cartographer"),
        "gis" -> List("GIS Developer", "Geospatial Analyst", "Cartographer"),
        "quantum" -> List("Quantum Computing Scientist", "Quantum Physicist", "Quantum Engineer"),
        "computing" -> List("Quantum Computing Scientist", "Quantum Physicist", "Quantum Engineer"),
        "quantum" -> List("Quantum Computing Scientist", "Quantum Physicist", "Quantum Engineer"),
        "financial" -> List("Fintech Developer", "Quantitative Analyst", "Financial Engineer"),
        "technology" -> List("Fintech Developer", "Quantitative Analyst", "Financial Engineer"),
        "fintech" -> List("Fintech Developer", "Quantitative Analyst", "Financial Engineer"),
        "human-computer" -> List("HCI Specialist", "UX Researcher", "UI/UX Designer"),
        "interaction" -> List("HCI Specialist", "UX Researcher", "UI/UX Designer"),
        "hci" -> List("HCI Specialist", "UX Researcher", "UI/UX Designer"),
        "compiler" -> List("Compiler Engineer", "Language Designer", "Software Engineer"),
        "design" -> List("UI/UX Designer", "Product Designer", "Graphic Designer"),
        "compilers" -> List("Compiler Engineer", "Language Designer", "Software Engineer")
)

//if the tokens list in the help(the main function) is not empty this function will get called
def greetUser(): String = {
    "Hi there! I'm Career Navigator, your tech career advisor.\nI can help you identify your skills, explore career paths, and navigate the job search."
}

def getUserPreferences(): String = {

// folderPath takes the name of the folder where chat history files are stored
  val folderPath = "conversations"
  val currentChatFilePath = s"$folderPath/currentchat.txt"

  // Check if the file exists
  val file = new File(currentChatFilePath)
  if (!file.exists()) {
    return null
  }

  // Read the first line from the file
  val firstLine = Source.fromFile(file).getLines().next()

  // Close the file
  Source.fromFile(file).close()

  // Return the first line
  return firstLine
}

def storeUserPreferences(userInput: String): Unit = {
   //the folder where the convo is going to be stored

  val folderPath = "conversations"
  // Create the folder if it doesn't exist
  val folder = new File(folderPath)
  if (!folder.exists()) {
    folder.mkdir()
  }
  // Open the file in append mode and write the user input and bot response
  val currentchatFileWriter = new BufferedWriter(new FileWriter(folderPath+"/"+getUserPreferences(), true))
  currentchatFileWriter.write(userInput + "\n")
  currentchatFileWriter.close()
}

//ParseInput return tokens(the final list of strings with the programming language,
//the skills,the topics ,fields that the user has entered as an input )
def parseInput(input: String): List[String] = {

// Replace any characters that are not letters (a-z, A-Z), numbers (#), plus sign (+), or period (.) with a space.
  // Convert the input string to lowercase, split it into words using spaces, and create a list of strings.
    var parseList: List[String] = input.replaceAll("[^a-zA-Z#+.]", " ").toLowerCase.split(' ').toList
    
     // Filter out any empty strings from the parseList.
    parseList = parseList.filter(_.nonEmpty)
  // Parselist is a list containing all non-empty strings extracted from the input after processing.

  // all the programming languages that the user can write while asking for  job 
    val programming_languages = List("python", "py", "java", "javascript", "c++", "cpp", "c#", 
        "swift", "go", "ruby", "php", "rust", "kotlin", "typescript", "perl", "scala", "lua", "r", 
        "haskell", "dart", "matlab", "lisp", "sql", "html","css", "bash", "c", "assembly language", 
        "clojure", "f#", "julia", "erlang", "groovy",".net")

// all the fields that the user can write while asking for  job 
    val fields = List("web", "development", "webdev", "mobile", "app", "development", "mobile", "dev", 
        "data", "science", "data", "analysis", "machine", "learning", "ml", "artificial", "intelligence", 
        "ai", "game", "development", "gamedev", "networking", "networks", "cybersecurity", "infosec", 
        "embedded", "systems", "embedded", "robotics", "scientific", "computing", "scientific", "computation", 
        "cloud", "computing", "cloud", "natural", "language", "processing", "nlp", "databases", 
        "database", "management", "software", "engineering", "software", "dev", "computer", "graphics", 
        "graphics", "operating", "systems", "os", "blockchain", "blockchaining", "big", "data", 
        "iot", "Internet", "of", "Things", "iot", "bioinformatics", "bioinfo", "virtual", "reality", 
        "vr", "augmented", "reality", "ar", "computer", "vision", "cv", "geographic", "information", 
        "systems", "gis", "quantum", "computing", "quantum", "financial", "technology", "fintech", 
        "human-computer", "interaction", "hci", "compiler", "design", "compilers")

        // all the topics that the user can write while asking for  job 
    val topics = List("data", "structures", "ds", "algorithms", "algo", "object-oriented", "programming", 
        "oop", "functional", "programming", "fp", "dynamic", "programming", "dp", "recursion", "rec", 
        "graph", "theory", "graphs", "search", "algorithms", "sorting", "algorithms", "greedy", 
        "algorithms", "dynamic", "programming", "dp", "machine", "learning", "ml", "deep", "learning", 
        "dl", "neural", "networks", "nns", "natural", "language", "processing", "nlp", "computer", 
        "vision", "cv", "image", "processing", "ip", "database", "management", "databases", "sql", 
        "nosql", "big", "data", "data", "mining", "data", "visualization", "dv", "parallel", 
        "computing", "concurrent", "programming", "parallelism", "concurrency", "operating", "systems", 
        "os", "networking", "network", "protocols", "cloud", "computing", "distributed", "systems", "security", 
        "cybersecurity", "infosec", "cryptography", "encryption", "blockchain", "web", "development", 
        "frontend", "development", "backend", "development", "full-stack", "development", "software", 
        "testing", "testing", "agile", "methodologies", "agile", "version", "control", "git")

// all the skills that the user can write while asking for  job 
    val skills = List("problem", "solving", "ps", "critical", "thinking", "communication", "skills", 
        "communication", "collaboration", "teamwork", "adaptability", "adaptation", "time", "management", 
        "leadership", "attention", "to", "detail", "attention", "to", "details", "creativity", "innovation", 
        "self-motivation", "self-management", "analytical", "skills", "analytical", "thinking", "technical", 
        "skills", "soft", "skills", "programming", "coding", "debugging", "troubleshooting", "testing", 
        "version", "control", "git", "agile", "methodologies", "agile", "project", "management", "presentation", 
        "skills", "public", "speaking", "writing", "skills", "documentation", "research", "skills", "problem", 
        "identification", "problem", "framing", "solution", "design", "decision", "making", "learning", 
        "agility", "emotional", "intelligence", "interpersonal", "skills", "negotiation", "conflict", 
        "resolution", "customer", "service", "user", "experience", "design", "ux", "design")
        
        //Tokens here is a filteration of the final list from parseList
        //to see if any of the words in this list match the programming languages available or the fields or the topics or the skills
        // that the user might enter 
    val tokens = parseList.filter(word => 
        programming_languages.contains(word)
        || fields.contains(word) 
        || topics.contains(word)
        || skills.contains(word))
    tokens
}

def generateResponse(query: String): String = {

      //Select the first element from the list of startTemplates in line 22
    val randomTemplate = Random.shuffle(startTemplates).head

    // get description
    val infoKeywords = List("description", "information", "info", "details", "more", "explain", "tell","overview", "about", "learn", "understand")
    var containsInfoKeywords: List[String] = List()
    infoKeywords.foreach { keyword =>if (query.replaceAll("[^a-zA-Z]", " ").toLowerCase.contains(keyword.toLowerCase)) {containsInfoKeywords = containsInfoKeywords :+ keyword}}
    
    // get salary
    val SalaryKeywords = List("much","salary","salaries","range","prices","yearly","average","makes","make","compensation")
    var containsSalaryKeywords: List[String] = List()
    SalaryKeywords.foreach { keyword =>if (query.replaceAll("[^a-zA-Z]", " ").toLowerCase.contains(keyword.toLowerCase)) {containsSalaryKeywords = containsSalaryKeywords :+ keyword}}

    // get responsibilities
    val ResponsibilitiesKeywords = List("resp","responsibilities","responsibilitie","responsibility")
    var containsResponsibilitiesKeywords: List[String] = List()
    ResponsibilitiesKeywords.foreach { keyword =>if (query.replaceAll("[^a-zA-Z]", " ").toLowerCase.contains(keyword.toLowerCase)) {containsResponsibilitiesKeywords = containsResponsibilitiesKeywords :+ keyword}}

    // get qualifications
    val QualificationKeywords = List("qualification","qualifications","requirements","requirement")
    var containsQualificationKeywords: List[String] = List()
    QualificationKeywords.foreach { keyword =>if (query.replaceAll("[^a-zA-Z]", " ").toLowerCase.contains(keyword.toLowerCase)) {containsQualificationKeywords = containsQualificationKeywords :+ keyword}}

    var roleName = ""

    for {job <- userSuggestedRandC 
        start <- 0 until (query.length - job.length + 1) if query.substring(start, start + job.length).equalsIgnoreCase(job)} { roleName = job}
    val filename = "datasets/jobs_DSQR.csv"
    val lines = Source.fromFile(filename).getLines().toList
    val header = lines.head.split(",").map(_.trim)
    val jobTitleIndex = header.indexOf("Job Title")
    val descriptionIndex = header.indexOf("Description")
    val salaryIndex  = header.indexOf("Salary Range")
    val qualificationIndex = header.indexOf("Qualifications")
    val responseIndex = header.indexOf("Responsibilities")
    var response = ""
    if (containsInfoKeywords.nonEmpty || containsSalaryKeywords.nonEmpty || containsQualificationKeywords.nonEmpty || containsResponsibilitiesKeywords.nonEmpty || roleName == "") {
        val alljobtitles = lines.tail.map(_.split(",")(jobTitleIndex))
        for {job <- alljobtitles 
            start <- 0 until (query.length - job.length + 1) if query.substring(start, start + job.length).equalsIgnoreCase(job)} { roleName = job.toLowerCase()}
        if (roleName != "") {
          var output = randomTemplate+roleName
            // Salary
            if(containsSalaryKeywords.nonEmpty){
                for (line <- lines.tail) {
                    val cols = line.split(",").map(_.trim)
                    if (cols(jobTitleIndex).toLowerCase() == roleName) {response = cols(salaryIndex)}}
                output = output +" makes around "+response+" per year."
            }
            // Qualifications
            if(containsQualificationKeywords.nonEmpty){
                for (line <- lines.tail) {
                    val cols = line.split(",").map(_.trim)
                    if (cols(jobTitleIndex).toLowerCase() == roleName) {response = cols(qualificationIndex)}}
                output = output +" qualifications are:\n"+response
            }
            // Responsibilities
            if(containsResponsibilitiesKeywords.nonEmpty){
                for (line <- lines.tail) {
                    val cols = line.split(",").map(_.trim)
                    if (cols(jobTitleIndex).toLowerCase() == roleName) {response = cols(responseIndex)}}
                output = output +" responsibilities are:\n"+response
            }
            // Description
            if(containsInfoKeywords.nonEmpty || (roleName != "" && containsInfoKeywords.isEmpty && containsQualificationKeywords.isEmpty && containsResponsibilitiesKeywords.isEmpty && containsSalaryKeywords.isEmpty)) {
                for (line <- lines.tail) {
                    val cols = line.split(",").map(_.trim)
                    if (cols(jobTitleIndex).toLowerCase() == roleName) {response = cols(descriptionIndex)}}
                output = output +" "+response
            }
            return output
        } else return ""
    }
    return s"Sorry, I couldn't find a description for $roleName."
}

def handleUserInput(input: String): String ={
    var response = "Sorry, I couldn't find any matching careers or roles based on your input.\n"+
            "Could you please provide information on the programming languages you're knowledgeable about," +
            "your skill set, and the fields or topics within the tech industry that pique your interest?"

    if (generateResponse(input) != "")
        response = generateResponse(input)
        return response
    
    val tokens = parseInput(input)    
    val SuggestedCandR = tokens.flatMap { 
        case token if careersAndRolesMap.contains(token) => careersAndRolesMap(token)
        case _ => List.empty[String]
    }
    userSuggestedRandC = userSuggestedRandC ++ SuggestedCandR
    userSuggestedRandC = userSuggestedRandC.distinct

    if (userSuggestedRandC.nonEmpty && tokens.nonEmpty) {
        val randomTemplate = Random.shuffle(responseTemplates).head
        val rolesList = userSuggestedRandC.map(role => s"  - $role").mkString("\n")
        response = randomTemplate.replace("{roles}", rolesList) + "\nPlease feel free to ask me about any role!"
        response 
    } 

    val gratitudeKeywords = List("merci", "shokran", "thx", "thanks", "thank", "appreciate", "grateful", "helpful", "awesome", "great", "fantastic", "wonderful", "amazing", "love", "good", "bye")
    var containsgratitudeKeywords: List[String] = List()
    gratitudeKeywords.foreach { keyword =>if (input.replaceAll("[^a-zA-Z]", " ").toLowerCase.contains(keyword.toLowerCase)) {containsgratitudeKeywords = containsgratitudeKeywords :+ keyword}}
    if (containsgratitudeKeywords.nonEmpty) {
        return "You're welcome! If you have any more questions or need further assistance, feel free to ask. :)"
    }

    response
}


def communication(output: String): Unit = {
  // Wipe the pyinput.txt file clean
  new PrintWriter("connection/pyinput.txt") {
    close()
  }
  // Write processed input to scalaoutput.txt
  val writer = new PrintWriter(new File("connection/scalaoutput.txt"))
  writer.write(output)
  writer.close()
}

@main def help(): Unit = {
  val startconvKeywords = List("hi","hello","good","morning","bonjour")
    while(true){
          //get the user input from the python file that have the input saved in it
      val userinput = Source.fromFile("connection/pyinput.txt").getLines().mkString("\n")
        if (userinput != ""){ 
          var tokens: List[String] = userinput.replaceAll("[^a-zA-Z#+.]", " ").toLowerCase.split(' ').toList
          tokens = tokens.filter(_.nonEmpty)
          tokens = tokens.filter(word => startconvKeywords.contains(word))
          tokens match {
            case Nil =>
              val userInteraction = handleUserInput(userinput)
              communication(userInteraction)
              storeUserPreferences(userinput) 
            case _ =>
              communication(greetUser())
              storeUserPreferences(userinput)
      }
    }
  }
}
