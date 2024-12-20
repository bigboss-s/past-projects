import { Field, InputType } from "@nestjs/graphql";
import { IsAlpha, IsAlphanumeric, IsNotEmpty } from "class-validator";
import { CreateQuestionInput } from "../../questions/dto/create-question.input";

@InputType()
export class CreateQuizInput {
    @IsNotEmpty()
    @Field()
    name: string;

    @IsNotEmpty()
    @Field()
    description: string;

    @Field(type => [CreateQuestionInput])
    questions: CreateQuestionInput[];
}