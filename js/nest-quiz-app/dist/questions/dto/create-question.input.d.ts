import { QuestionType } from "../questions.entity";
import { CreateAnswerInput } from "../../answers/dto/create-answer.input";
export declare class CreateQuestionInput {
    questionString: string;
    type: QuestionType;
    answers: CreateAnswerInput[];
}
