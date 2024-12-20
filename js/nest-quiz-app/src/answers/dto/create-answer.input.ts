import { Field, InputType } from "@nestjs/graphql";
import { IsAlphanumeric, IsBoolean, IsNotEmpty, IsNumber } from "class-validator";

@InputType()
export class CreateAnswerInput {
    @IsNotEmpty()
    @Field()
    answerString: string;

    @IsBoolean()
    @Field({nullable: true})
    isCorrect?: boolean;

    @IsNumber()
    @Field({nullable: true})
    order?: number;
}