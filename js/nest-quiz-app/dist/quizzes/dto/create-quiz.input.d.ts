import { CreateQuestionInput } from "../../questions/dto/create-question.input";
export declare class CreateQuizInput {
    name: string;
    description: string;
    questions: CreateQuestionInput[];
}
