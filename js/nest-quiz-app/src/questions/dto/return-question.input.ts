import { Field, InputType, Int, ObjectType } from "@nestjs/graphql";
import { ReturnAnswerDTO } from "../../answers/dto/return-answer.input";
import { QuestionType } from "../questions.entity";

@InputType()
export class ReturnQuestionDTO {
    @Field(type => Int)
    id: number;

    @Field(type => QuestionType)
    type: QuestionType;

    @Field(type => [ReturnAnswerDTO])
    answers: ReturnAnswerDTO[];
}