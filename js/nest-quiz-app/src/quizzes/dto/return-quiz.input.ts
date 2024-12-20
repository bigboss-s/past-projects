import { Field, InputType, Int } from "@nestjs/graphql";
import { ReturnQuestionDTO } from "../../questions/dto/return-question.input";

@InputType()
export class ReturnQuizDTO{
    
    @Field(type => Int)
    id: number;

    @Field(type => [ReturnQuestionDTO])
    questions: ReturnQuestionDTO[];
}