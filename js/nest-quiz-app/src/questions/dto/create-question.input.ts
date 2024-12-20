import { Field, InputType } from "@nestjs/graphql";
import { IsAlpha, IsAlphanumeric, IsNotEmpty } from "class-validator";
import { QuestionType } from "../questions.entity";
import { CreateAnswerInput } from "../../answers/dto/create-answer.input";

@InputType()
export class CreateQuestionInput {
    @IsNotEmpty()
    @Field()
    questionString: string;

    @Field(type => QuestionType)
    type: QuestionType;

    @Field(type => [CreateAnswerInput])
    answers: CreateAnswerInput[]

}