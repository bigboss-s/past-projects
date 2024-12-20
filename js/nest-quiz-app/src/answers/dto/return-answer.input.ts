import { Field, InputType, Int, ObjectType } from "@nestjs/graphql";

@InputType()
export class ReturnAnswerDTO {

    @Field(type => Int)
    id: number;

    @Field({nullable: true})
    answerString?: string;

    @Field({nullable: true})
    isCheckedTrue?: boolean;

    @Field({nullable: true})
    checkedOrder?: number;
}